package org.jiranibora.com.loans;

import org.jiranibora.com.application.TransactionDto;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.loans.dto.LoanApplicationDto;
import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.LoanStatement;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.OverdueCharges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
@EnableScheduling
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final AuthenticationRepository authRepository;
    private final Utility utility;
    private final LoanStatementRepo loanStatementRepo;
    private final OverdueChargesRepository overdueChargesRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository,
            AuthenticationRepository authRepository, Utility utility, OverdueChargesRepository overdueChargesRepository,
            LoanStatementRepo loanStatementRepo) {
        this.loanRepository = loanRepository;
        this.authRepository = authRepository;
        this.utility = utility;
        this.loanStatementRepo = loanStatementRepo;
        this.overdueChargesRepository = overdueChargesRepository;

    }

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
            Double owedAmount = loanStatement.get().getInterest() + loanStatement.get().getPrinciple();
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

        LoanApplication loanApplication = loanRepository.findById(loan_id).get();
        if (loanApplication != null) {
            if (!loanApplication.viewed) {
                // update the loan to viewed
                loanApplication.setViewed(true);
                if (Objects.equals(action, "approve")) {
                    loanApplication.setStatus("Approved");
                } else {
                    loanApplication.setStatus("Declined");
                }
                loanRepository.saveAndFlush(loanApplication);
                // Now initialize the loan statement
                // Determine the interest
                Double interest = 0.0;
                if (loanApplication.owner) {
                    interest = .2;
                } else {
                    interest = .3;
                }
                LoanStatement loanStatement = LoanStatement.builder()

                        .loanId(loanApplication)
                        .principle(Double.valueOf(loanApplication.getAmount()))
                        .expectedOn(LocalDateTime.now().plusMinutes(loanApplication.getDuration()))
                        .interest(loanApplication.amount * loanApplication.duration
                                * interest)

                        .build();
                loanStatementRepo.saveAndFlush(loanStatement);
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

    public LoanRes repayLoan(Double amount) {
        // Check if the member is logged in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return LoanRes.builder().code(403).message("You are not authenticated").build();
        }
        // Check whether the member has a loan
        Member loggedInMember = authRepository.findMemberByMemberId(authentication.getName());

        log.error("The logged in members has the following loans" + loggedInMember.getMyLoans());

        Optional<LoanStatement> existingLoanObj = loanStatementRepo.findAllByMemberId(authentication.getName()).stream()
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

        if (Double.valueOf(amount) > (existingInterest + existingPrincipal)) {
            return LoanRes.builder().code(417)
                    .message("Loan balance less than the amount. Outstanding loan amount is KES "
                            + (existingInterest + existingPrincipal))
                    .build();
        }
        // Check if the amount is just for the principal only
        Double newPrincipal = existingPrincipal - amount.doubleValue();
        Double newInterest = existingInterest;
        if (newPrincipal < 0) {
            newInterest = existingInterest + newPrincipal;
            newPrincipal = Integer.valueOf(0).doubleValue();
        }

        existingLoan.setInterest(newInterest);
        existingLoan.setPrinciple(newPrincipal);
        loanStatementRepo.saveAndFlush(existingLoan);
        // Record the transaction here

        TransactionDto transactionDto = TransactionDto.builder()
                .amount(amount)
                .memberId(loggedInMember)
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
                each -> each.getExpectedOn().isBefore(LocalDateTime.now())
                        && each.getPrinciple() > 0)
                .collect(Collectors.toList());
        if (overdue.size() > 0) {
            Double loanIterestPercentage = 0.0;
            Long timeElapsedSinceLastUpdate = 1L;

            for (LoanStatement loanStatement : overdue) {
                Double overdueCharge = 0.0;
                if (loanStatement.getLoanId().getOwner()) {
                    loanIterestPercentage = .2;
                } else {
                    loanIterestPercentage = .3;
                }
                // Incase of a system downtime and the update is not recorded please get the
                // elapsed time
                timeElapsedSinceLastUpdate = (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                        - loanStatement.getExpectedOn().toEpochSecond(ZoneOffset.UTC)) / 60;
                System.out.println(timeElapsedSinceLastUpdate);        

                overdueCharge = loanIterestPercentage * loanStatement.getPrinciple() * (timeElapsedSinceLastUpdate+1);

                loanStatement.setInterest(
                        loanStatement.getInterest() + overdueCharge);
                loanStatement.setExpectedOn(LocalDateTime.now().plusMinutes(1));
                loanStatementRepo.saveAndFlush(loanStatement);
                // Add the record as well in the overdue charges table

                OverdueCharges overdueCharges = OverdueCharges.builder()
                        .loanId(loanStatement.getLoanId())
                        .overdueCharge(overdueCharge)
                        .lastModified(LocalDateTime.now())

                        .build();
                overdueChargesRepository.saveAndFlush(overdueCharges);
                System.out.println("The account for " + loanStatement.getLoanId().getMemberId().getFullName()
                        + " was charged KES " + overdueCharge + " for late additional charges");

            }
        } else {
            System.out.println("There are no loan defaulters in the system currently");
        }
    }
}
