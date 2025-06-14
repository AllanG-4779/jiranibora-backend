package org.jiranibora.com.loans;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.jiranibora.com.application.TransactionDto;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.MemberContributionRepository;
import org.jiranibora.com.loans.dto.*;
import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.LoanStatement;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.OverdueCharges;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.jiranibora.com.twilio.SMSending;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final AuthenticationRepository authRepository;
    private final Utility utility;
    private final LoanStatementRepo loanStatementRepo;
    private final SMSending smsSending;
    private final OverdueChargesRepository overdueChargesRepository;
    private final MemberContributionRepository memberContributionRepository;
    final int updateTimeline = 1;

    public LoanRes addLoan(LoanApplicationDto loanApplicationDto) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)) {
            throw new Exception("You are not authenticated");
        }

        Member member = authRepository.findMemberByMemberId(authentication.getName());
        // Check if there is an existing application on file;
        LoanApplication alreadyApplied = loanRepository.findByMemberId(member);
        if (alreadyApplied != null) {
            return LoanRes.builder().code(403).message("You have an existing loan please wait for it to be approved.")
                       .build();
        }
        // Check if the applicant has a loan that has not been repaid;
        Optional<LoanStatement> loanStatement = loanStatementRepo.findAllByMemberId(member.getMemberId()).stream()
                .filter(each -> each.getPrinciple() > 0 || each.getInterest() > 0).findFirst();
        if (loanStatement.isPresent()) {
            double owedAmount = loanStatement.get().getInterest() + loanStatement.get().getPrinciple();
            return LoanRes.builder().code(403).message("You have a loan pending repayment, KES " + owedAmount).build();
        }

        LoanApplication loanApplication = LoanApplication.builder()
                .applicationId("LNA" + utility.randomApplicationID())
                .amount(loanApplicationDto.getAmount())
                .dateApplied(LocalDateTime.now())
                .duration(loanApplicationDto.getDuration())
                .fullName(loanApplicationDto.getFullName())
                .nationalId(loanApplicationDto.getNationalId())
                .owner(loanApplicationDto.getOwner())
                .memberId(member)
                .status("Pending")
                .viewed(false)
                .build();
        loanRepository.saveAndFlush(loanApplication);
        return LoanRes.builder().code(201).message("Loan application was submitted").build();
    }

    public LoanRes takeAction(String loan_id, String action) throws Exception {

        Optional<LoanApplication> loanApplication = loanRepository.findById(loan_id);
        if (loanApplication.isPresent()) {
            if (!loanApplication.get().viewed) {
                // update the loan to viewed
                loanApplication.get().setViewed(true);
                loanApplication.get().setDateViewed(LocalDateTime.now());
                if (Objects.equals(action, "approve")) {
                    loanApplication.get().setStatus("Approved");
                    Double interest = 0.0;
                    if (loanApplication.get().owner) {
                        interest = .2;
                    } else {
                        interest = .3;
                    }
                    LoanStatement loanStatement = LoanStatement.builder()

                            .loanId(loanApplication.get())
                            .principle(Double.valueOf(loanApplication.get().getAmount()))
                            .expectedOn(LocalDateTime.now().plusMinutes(loanApplication.get().getDuration() *   updateTimeline ))
                            .interest(loanApplication.get().amount * loanApplication.get().duration
                                    * interest)

                            .build();
                    loanStatementRepo.saveAndFlush(loanStatement);
                } else {
                    loanApplication.get().setStatus("Declined");
                }
                loanRepository.saveAndFlush(loanApplication.get());
                // Now initialize the loan statement
                // Determine the interest
             
                return LoanRes.builder().code(200)
                        .message("The loan was " + action + " successfully").build();

            }
        } else {
            return LoanRes.builder()
                    .code(404)
                    .message("No loan with the ID provided was found").build();

        }
        return null;
    }

    // Loan repayment endpoint
    @Transactional
    public LoanRes repayLoan(Double amount, String memberId) throws JsonProcessingException {
        // Check if the member is logged in

        Member member;
        if (memberId != null) {
            member = authRepository.findMemberByMemberId(memberId);
        } else {
            member = utility.getAuthentication();
        }
        if (member == null) {
            return LoanRes.builder().code(403).message("You are not authenticated").build();
        }

        // Check whether the member has a loan
        // Member loggedInMember =
        // authRepository.findMemberByMemberId(authentication.getName());
        //
        // log.error("The logged in members has the following loans" +
        // loggedInMember.getMyLoans());

        Optional<LoanStatement> existingLoanObj = loanStatementRepo.findAllByMemberId(member.getMemberId()).stream()
                .filter(each -> (each.getPrinciple() + each.getInterest()) > 0.0)
                .findFirst();

        if (existingLoanObj.isEmpty()) {
            return LoanRes.builder().code(404).message("You have no outstanding loan").build();
        }
        // Make the actual payment

        // Update the loan database
        LoanStatement existingLoan = existingLoanObj.get();
        Double existingInterest = existingLoan.getInterest();
        Double existingPrincipal = existingLoan.getPrinciple();

        if (amount > (existingInterest + existingPrincipal)) {
            return LoanRes.builder().code(417)
                    .message("Loan balance less than the amount. Outstanding loan amount is KES "
                            + (existingInterest + existingPrincipal))
                    .build();
        }
        // Check if the amount is just for the principal only
        double newPrincipal = existingPrincipal - amount;
        Double newInterest = existingInterest;
        if (newPrincipal < 0) {
            newInterest = existingInterest + newPrincipal;
            newPrincipal = Integer.valueOf(0).doubleValue();
        }

        existingLoan.setInterest(newInterest);
        existingLoan.setPrinciple(newPrincipal);
        loanStatementRepo.saveAndFlush(existingLoan);
        // Record the transaction here
        double pending =newPrincipal+newInterest;

        TransactionDto transactionDto = TransactionDto.builder()
                .amount(amount)
                .memberId(member)
                .paymentCategory("Loan Repayment")
                .serviceId(existingLoan.getLoanId().applicationId)
                .transactionDate(LocalDateTime.now())

                .build();


        utility.addTransaction(transactionDto);

        return LoanRes.builder().code(200).message("Transaction successful").build();
    }

    @Scheduled(initialDelay = 4000L, fixedDelayString = "PT30S")
    @Transactional
    public void findAndUpdateInterestsForOverdueLoans() {
       
        List<LoanStatement> overdue = loanStatementRepo.findAll().stream().filter(
                each -> each.getPrinciple() > 0 && LocalDateTime.now().isAfter(each.getExpectedOn()))
                .collect(Collectors.toList());

        if (overdue.size() > 0) {
            Double loanIterestPercentage;
            double timeElapsedSinceLastUpdate;

            for (LoanStatement loanStatement : overdue) {
                Double overdueCharge = 0.0;
                if (loanStatement.getLoanId().getOwner()) {
                    loanIterestPercentage = .02;
                } else {
                    loanIterestPercentage = .03;
                }
                // Incase of a system downtime and the update is not recorded please get the
                // elapsed time
                if (LocalDateTime.now().equals(loanStatement.getExpectedOn())) {
                    overdueCharge = loanStatement.getPrinciple() * loanIterestPercentage;
                    loanStatement.setExpectedOn(LocalDateTime.now().plusMinutes(updateTimeline));
                    loanStatement.setInterest(overdueCharge + loanStatement.getInterest());

                } else if (LocalDateTime.now().isAfter(loanStatement.getExpectedOn())) {
                    // get elapsed time
                    Long secondsElapsed = (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                            - loanStatement.getExpectedOn().toEpochSecond(ZoneOffset.UTC));

                    timeElapsedSinceLastUpdate = (secondsElapsed / (updateTimeline * 60)) + 1;
                    log.error("" + secondsElapsed);

                    if (timeElapsedSinceLastUpdate >= 1) {
                        // Update the loan
                        overdueCharge = (loanStatement.getPrinciple() * loanIterestPercentage
                                * Math.floor(timeElapsedSinceLastUpdate));
                        loanStatement.setInterest(overdueCharge + loanStatement.getInterest());

                        log.info("Due to system down time " + loanStatement.getLoanId().getMemberId().getFullName()
                                + " was charged " + timeElapsedSinceLastUpdate + " times");

                    }
                    loanStatement.setExpectedOn(LocalDateTime.now()
                            .plusSeconds((updateTimeline * 60) - secondsElapsed % (updateTimeline * 60)));
                    log.info("System down time recovered as the next update will take place after "
                            + ((updateTimeline * 60) + (-(secondsElapsed % (updateTimeline * 60)))));
                    loanStatementRepo.saveAndFlush(loanStatement);
                }
                loanStatementRepo.saveAndFlush(loanStatement);

                // Add the record as well in the overdue charges table

                OverdueCharges overdueCharges = OverdueCharges.builder()
                        .loanId(loanStatement.getLoanId())
                        .overdueCharge(overdueCharge)
                        .lastModified(LocalDateTime.now())

                        .build();
                overdueChargesRepository.saveAndFlush(overdueCharges);

            }
        }
    }
//    Get all loans applied
    public List<LoanResponseDto> getAllLoans(){
        List<LoanStatement> loanStatements = loanStatementRepo.findAll();
        return loanStatements.stream().map(this::buildLoanDto).collect(Collectors.toList());
    }


    public MemberLoanProfileDto getAllStatementsforUser() {
        Member member = utility.getAuthentication();
        if (member == null){
            log.error("User access token was not provided");
        }else {


            List<LoanStatement> individualStatement = loanStatementRepo.findAllByMemberId(member.getMemberId());

            List<LoanResponseDto> loanResList = individualStatement.stream().map(this::buildLoanDto)
                    .collect(Collectors.toList());

            LoanSummaryDto loanSummary = LoanSummaryDto.builder()
                    // Take all time overdue charges plus the initial interest for each loan.
                    .allTimeInterest(overdueChargesRepository.findOverdueChargesByMemberId(member).stream()
                            .mapToDouble(OverdueCharges::getOverdueCharge).sum()
                            + loanStatementRepo.findAllByMemberId(member.getMemberId()).stream()
                            .mapToDouble(
                                    each -> each.getLoanId().getOwner() ? each.getLoanId().getAmount() * 0.2
                                            * each.getLoanId().duration
                                            : each.getLoanId().getAmount() * 0.3 * each.getLoanId().duration)
                            .sum())

                    .allTimeBorrowing(loanRepository.findTotalLoanDisbursedToMember(member, "Approved")
                            .stream().mapToDouble(LoanApplication::getAmount).sum())
                    .declined(loanRepository.findByStatusAndMemberId("Declined", member).size())
                    .build();
            return MemberLoanProfileDto.builder().loanResponseList(loanResList).loanSummary(loanSummary).build();
        }
      return  null;
    }

    public LoanResponseDto buildLoanDto(LoanStatement loanStatement) {

        Double initialInterest = loanStatement.getLoanId().getAmount() * loanStatement.getLoanId().getDuration()
                * (loanStatement.getLoanId().getOwner() ? 0.2 : .3);

        Double totalInterestCharged = (overdueChargesRepository
                .findInterestCharged(loanStatement.getLoanId()).stream().mapToDouble(OverdueCharges::getOverdueCharge).sum());

        Double outStandingAmount = loanStatement.getInterest() + loanStatement.getPrinciple();

        return LoanResponseDto.builder()
                .loanId(loanStatement.getLoanId().getApplicationId())
                .amount(Double.valueOf(loanStatement.getLoanId().getAmount()))
                .dateApplied(loanStatement.getLoanId().getDateApplied())
                .dateApproved(loanStatement.getLoanId().getDateViewed())
                .initialDuration(loanStatement.getLoanId().getDuration())
                .initialInterest(initialInterest)
                .outstandingAmount(outStandingAmount)
                .member(loanStatement.getLoanId().getMemberId().getFullName().split("ID")[0])
                .memberId(loanStatement.getLoanId().getMemberId().getMemberId())
                .extraInterest(totalInterestCharged)
                .status(outStandingAmount > 0 ? "In progress" : "Completed")
                .build();
    }

    public List<LoanApplicationDto> findAllNewApplications() {
        return loanRepository.findByViewed(false).stream().map(each -> LoanApplicationDto.builder()
                .amount(each.getAmount())
                .duration(each.getDuration())
                .fullName(each.getMemberId().getPrevRef().getFirstName() + " "
                        + each.getMemberId().getPrevRef().getLastName())
                .owner(each.getOwner())
                .memberId(each.getMemberId().getMemberId())
                .loanId(each.applicationId)
                .contribution(memberContributionRepository.findByMemberId(each.memberId).stream()
                        .mapToDouble(
                                contribution -> Double.parseDouble(contribution.getMemberId().getPrevRef().getAmount()))
                        .sum())
                .build()).collect(Collectors.toList());

    }
}
