package org.jiranibora.com.loans;

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

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final AuthenticationRepository authRepository;
    private final Utility utility;
    private final LoanStatementRepo loanStatementRepo;

    @Autowired
    public LoanService(LoanRepository loanRepository,
            AuthenticationRepository authRepository, Utility utility, LoanStatementRepo loanStatementRepo) {
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

    public Integer takeAction(String loan_id, String action) throws Exception {

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
                return 0;      

            }
        } else {
            throw new Exception("No application found on file");
        }
      return null;
    }
}
