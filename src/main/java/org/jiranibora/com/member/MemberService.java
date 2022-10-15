package org.jiranibora.com.member;

import java.util.List;

import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.meetings.MeetingRepository;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.payment.FineRepository;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberService {
    private final MeetingRepository meetingRepository;
    private final AuthenticationRepository authenticationRepository;
    private final FineRepository fineRepository;

    public SecretaryFineDto getMemberToBefined(String parameter) {
        // Find if there is a meeting open for fine allocation
        Meeting meetingExist = meetingRepository.findByStatus("ON");
        Member member = authenticationRepository.findMemberByMemberId(parameter);
        if (!(meetingExist != null) || member == null) {
            return null;
        }

        // find the fines in the users account
        Double pendingFines = fineRepository.findByPaidAndMemberId(false, member).stream()
                .mapToDouble(each -> each.getFineCategory().getChargeableAmount()).sum();

        return SecretaryFineDto.builder().meetingId(meetingExist.getMeetingId()).existingFine(pendingFines)
                .memberName(member.getPrevRef().getFirstName() + " " + member.getPrevRef().getLastName())
                .memberId(member.getMemberId()).build();
    }

}
