package org.jiranibora.com.penalty;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.jiranibora.com.application.TransactionDto;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Penalty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PenaltyService {
    private final PenaltyRepository penaltyRepository;
    private final AuthenticationRepository authenticationRepository;
    private final Utility utility;

    @Autowired
    public PenaltyService(PenaltyRepository penaltyRepository, AuthenticationRepository authenticationRepository,
            TransactionRepository transactionRepository, Utility utility) {
        this.penaltyRepository = penaltyRepository;
        this.authenticationRepository = authenticationRepository;
        this.utility = utility;

    }

    @Transactional
    public PenaltyResponse resolvePenaltyService(String penaltyId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)) {
            return PenaltyResponse.builder().code(403).message("You are not authenticated").build();
        }
        Member member = authenticationRepository.findMemberByMemberId(authentication.getName());

        Penalty penaltyToResolve = penaltyRepository.findByPenCode(penaltyId);

        if (penaltyToResolve == null) {
            return PenaltyResponse.builder().code(404).message("The ID provided was not found").build();

        }
        // Checking whether the ID provided belongs to the member
        if (!penaltyToResolve.getMemberId().getMemberId().equals(member.getMemberId())) {
            return PenaltyResponse.builder().code(409)
                    .message("Unless you are very generous, that penalty you are about to pay doesn't belong to you")
                    .build();
        }

        // If no other issue arise, make the payment for the penalty

        // PAYMENT API
        // saving results to the database;
        penaltyToResolve.setStatus("Paid");

        penaltyRepository.saveAndFlush(penaltyToResolve);
        // The other transaction
        TransactionDto currentTransaction = TransactionDto.builder()
                .amount(penaltyToResolve.getAmount())
                .memberId(member)
                .paymentCategory("Penalty")
                .serviceId(penaltyId)
                .transactionDate(LocalDateTime.now())
                .build();
        utility.addTransaction(currentTransaction);

        return PenaltyResponse.builder().message("Your payment was successful").code(200).build();
    }

    

}
