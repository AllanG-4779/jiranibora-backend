package org.jiranibora.com.loans;

import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.loans.dto.LoanApplicationDto;
import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final AuthenticationRepository authRepository;
    private final Utility utility;
    @Autowired
    public LoanService(LoanRepository loanRepository, AuthenticationRepository authRepository, Utility utility){
        this.loanRepository=loanRepository;
        this.authRepository=authRepository;
        this.utility = utility;
    }

    public LoanApplicationDto addLoan(LoanApplicationDto loanApplicationDto) throws Exception {
     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     if(!authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)){
         throw new Exception("You are not authenticated");
     }



     Member member = authRepository.findMemberByMemberId(authentication.getName());
        //     Check if there is an existing application on file;
        LoanApplication alreadyApplied = loanRepository.findByMemberId(member);
        if(alreadyApplied!=null){
          throw new UnsupportedOperationException("Some other loan has not been processed");
        }


        LoanApplication loanApplication = LoanApplication.builder()
                .applicationId("LNA"+utility.randomApplicationID())
                .amount(loanApplicationDto.getAmount())
                .dateApplied(LocalDateTime.now())
                .duration(loanApplicationDto.getDuration())
                .fullName(loanApplicationDto.getFullName())
                .nationalId(loanApplicationDto.getNationalId())
                .owner(loanApplicationDto.getOwner())
                .memberId(member)
                .status(Boolean.FALSE)
                .build();
        loanRepository.saveAndFlush(loanApplication);
        return loanApplicationDto;
    }
}
