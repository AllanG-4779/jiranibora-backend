package org.jiranibora.com.payment;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.jiranibora.com.application.TransactionDto;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.meetings.MeetingRepository;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.FineCategory;
import org.jiranibora.com.models.FinePrimaryKey;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Penalty;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PenaltyRepository penaltyRepository;
    private final FineRepository fineRepository;
    private final Utility utility;
    private final MeetingRepository meetingRepository;
    private final FineCategoryRepository fineCategoryRepository;

    @Autowired
    public PaymentService(PenaltyRepository penaltyRepository,
            TransactionRepository transactionRepository, Utility utility, FineRepository fineRepository,
            MeetingRepository meetingRepository,
            FineCategoryRepository fineCategoryRepository) {
        this.penaltyRepository = penaltyRepository;
        this.fineRepository = fineRepository;
        this.utility = utility;
        this.meetingRepository = meetingRepository;
        this.fineCategoryRepository = fineCategoryRepository;

    }

    @Transactional
    public PaymentResponse resolvePenaltyService(String penaltyId) {

        Member member = utility.getAuthentication();

        if (member == null) {
            return PaymentResponse.builder().code(403).message("You are not authenticated").build();
        }

        Penalty penaltyToResolve = penaltyRepository.findByPenCode(penaltyId);

        if (penaltyToResolve == null) {
            return PaymentResponse.builder().code(404).message("The ID provided was not found").build();

        }
        // Checking whether the ID provided belongs to the member
        if (!penaltyToResolve.getMemberId().getMemberId().equals(member.getMemberId())) {
            return PaymentResponse.builder().code(409)
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

        return PaymentResponse.builder().message("Your payment was successful").code(200).build();
    }

    @Transactional
    public PaymentResponse resolveFineService(String category, String meetingId) {

        Member member = utility.getAuthentication();

        if (member == null) {
            return PaymentResponse.builder().code(403).message("You are not authenticated").build();
        }
        // Find the meeting for which the fine is
        Optional<Meeting> meetingExist = meetingRepository.findById(meetingId);
        if (meetingExist.isEmpty()) {
            return PaymentResponse.builder().code(404).message("No fine data was found").build();
        }
        Meeting meeting = meetingExist.get();
        // Find the fine category

        FineCategory fineCategory = fineCategoryRepository.findByFineName(category);

        if (fineCategory == null) {
            return PaymentResponse.builder().code(404).message("The fine category with that ID was not found").build();
        }
        // build the primary key for the fine

        FinePrimaryKey finePrimaryKey = FinePrimaryKey.builder()
                .fineCategory(fineCategory.getFineCategoryId())
                .meetingId(meeting.getMeetingId())
                .memberId(member.getMemberId())
                .build();

        Optional<Fine> fineToResolveDoExist = fineRepository.findById(finePrimaryKey);

        if (fineToResolveDoExist.isEmpty()) {
            return PaymentResponse.builder().code(404).message("Fine you are about to pay was not found").build();
        }

        Fine fineToResolve = fineToResolveDoExist.get();


        // Checking whether the ID provided belongs to the member
        if (!fineToResolve.getMemberId().getMemberId().equals(member.getMemberId())) {
            return PaymentResponse.builder().code(409)
                    .message("Unless you are very generous, the fine you are about to pay doesn't belong to you")
                    .build();
        }

        // If no other issue arise, make the payment for the penalty

        // PAYMENT API
        // saving results to the database;
        fineToResolve.setPaid(true);

        fineRepository.saveAndFlush(fineToResolve);
        // The other transaction
        TransactionDto currentTransaction = TransactionDto.builder()
                .amount(fineToResolve.getFineCategory().getChargeableAmount())
                .memberId(member)
                .paymentCategory("Fine")
                .serviceId(finePrimaryKey.getFineCategory())
                .transactionDate(LocalDateTime.now())
                .build();
        utility.addTransaction(currentTransaction);

        return PaymentResponse.builder().message("Your payment was successful").code(200).build();
    }

}
