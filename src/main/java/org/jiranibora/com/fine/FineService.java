package org.jiranibora.com.fine;

import java.time.LocalDateTime;

import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.meetings.MeetingRepository;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.FineCategory;
import org.jiranibora.com.models.FinePrimaryKey;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.payment.FineCategoryRepository;
import org.jiranibora.com.payment.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineCategoryRepository fineCategoryRepository;
    private final FineRepository fineRepository;
    private final AuthenticationRepository authenticationRepository;
    private final MeetingRepository meetingRepository;

    public FineRes addFine(String meetingId, String memberId, String fineCategory) {
        // Check if there is fine with that Id
        FineCategory fineCategoryExist = fineCategoryRepository.findByFineName(fineCategory);
        if (fineCategoryExist == null) {
            return FineRes.builder().code(404).message("Fine category not found").build();
        }
        Member memberExist = authenticationRepository.findMemberByMemberId(memberId);

        if (memberExist == null) {
            return FineRes.builder().code(404).message("Invalid member ID").build();
        }
        Meeting meetingExist = meetingRepository.findById(meetingId).orElse(null);

        if (meetingExist == null || meetingExist.getStatus().equals("CLOSED")) {

            return FineRes.builder().code(404).message("A fine is only applicable to valid and open meeting").build();

        }

        FinePrimaryKey finePrimaryKey = FinePrimaryKey.builder()
                .memberId(memberId)
                .meetingId(meetingId)
                .fineCategory(fineCategoryExist.getFineCategoryId())
                .build();
        Fine fine = Fine.builder()
                .dateEnforced(LocalDateTime.now())
                .fineId(finePrimaryKey)
                .meetingId(meetingExist)
                .memberId(memberExist)
                .fineCategory(fineCategoryExist)
                .paid(false)
                .build();

        fineRepository.saveAndFlush(fine);
        return FineRes.builder().code(200).message("Fine added successfully to account").build();

    }

}
