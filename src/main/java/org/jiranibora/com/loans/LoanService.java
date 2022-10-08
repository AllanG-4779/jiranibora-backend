package org.jiranibora.com.loans;

import org.jiranibora.com.application.TransactionDto;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.loans.dto.LoanApplicationDto;
import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.LoanStatement;
import org.jiranibora.com.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final AuthenticationRepository authRepository;
    private final Utility utility;
    private final LoanStatementRepo loanStatementRepo;

    @Autowired
    public LoanService(LoanRepository loanRepository,
            AuthenticationRepository authRepository, Utility utility,
            LoanStatementRepo loanStatementRepo) {
        this.loanRepository = loanRepository;
        this.authRepository = authRepository;
        this.utility = utility;
        this.loanStatementRepo = loanStatementRepo;

    }

    public LoanApplicationDto addLoan(LoanApplicationDto loanApplicationDto) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)) {
            throw new Exception("You are not authenticated");
        }

        Member member = authRepository.findMemberByMemberId(authentication.getName());
        // Check if there is an existing application on file;
        LoanApplication alreadyApplied = loanRepository.findByMemberId(member);
        if (alreadyApplied != null) {
            throw new UnsupportedOperationException("Some other loan has not been processed");
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
        return loanApplicationDto;
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
                        .issuedAt(LocalDateTime.now())
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

    public LoanRes repayLoan(Integer amount) {
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
}
