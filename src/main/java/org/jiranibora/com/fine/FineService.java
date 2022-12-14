package org.jiranibora.com.fine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.meetings.MeetingRepository;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.FineCategory;
import org.jiranibora.com.models.FinePrimaryKey;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.payment.FineCategoryRepository;
import org.jiranibora.com.payment.FineRepository;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
public class FineService {

    private final FineCategoryRepository fineCategoryRepository;
    private final FineRepository fineRepository;
    private final AuthenticationRepository authenticationRepository;
    private final MeetingRepository meetingRepository;
    private Utility utility;

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
        List<Fine> existingFines = fineRepository.findAllByMeetingIdAndMemberId(meetingExist, memberExist);
        if (fineCategory.equals("lateness")) {
            if (existingFines.stream().anyMatch(each -> each.getFineCategory().getFineName().equals("absenteeism"))) {
                return FineRes.builder().code(403).message("Fine cannot be applied, member is already marked as absent")
                        .build();

            }
        }
        if (fineCategory.equals("absenteeism")) {
            if (existingFines.stream().anyMatch(each -> each.getFineCategory().getFineName().equals("lateness"))) {
                return FineRes.builder().code(403).message("Fine cannot be applied, member is already marked as late")
                        .build();

            }
        }
        if (fineCategory.equals("language")) {
            if (existingFines.stream().anyMatch(each -> each.getFineCategory().getFineName().equals("absenteeism"))) {
                return FineRes.builder().code(403).message("Fine cannot be applied. The member is already marked as absent")
                        .build();

            }
        }
        if (fineCategory.equals("absenteeism")) {
            if (existingFines.stream().anyMatch(each -> each.getFineCategory().getFineName().equals("lateness"))) {
                return FineRes.builder().code(403).message("A member cannot be late and absent at the same tme")
                        .build();

            }
        }



        FinePrimaryKey finePrimaryKey = FinePrimaryKey.builder()
                .memberId(memberId)
                .meetingId(meetingId)
                .fineCategory(fineCategoryExist.getFineCategoryId())
                .build();
        if (fineRepository.findById(finePrimaryKey).isPresent()) {
            return FineRes.builder().code(409).message(fineCategory + " fine has already been applied to account")
                    .build();

        }
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

    public List<PendingFinesDto> getAllPending() {
        List<Fine> allPending = fineRepository.findByPaid(false);

        return allPending.stream().map(each -> PendingFinesDto.builder()
                .amount(each.getFineCategory().getChargeableAmount())
                .dateFined(each.getDateEnforced())
                .name(each.getMemberId().getPrevRef().getFirstName() + " "
                        + each.getMemberId().getPrevRef().getLastName())
                .memberId(each.getMemberId().getMemberId())
                .meetingId(each.getMeetingId().getMeetingId())
                .phone(each.getMemberId().getPrevRef().getPhoneNumber())
                .fine(each.getFineCategory().getFineName())
                .build()).collect(Collectors.toList());

    };

    public SecretaryHomePageDto giveSecreataryHomepageSomeData() {
        // get all the meetings
        Long meetingCount = meetingRepository.count();
        // Total pending fines
        List<PendingFinesDto> pending = fineRepository.findByPaid(false).stream()
                .map(this::buildPendingFineObject).collect(Collectors.toList());
        Double paidFines = fineRepository.findByPaid(true).stream()
                .mapToDouble(each -> each.getFineCategory().getChargeableAmount()).sum();
        Double totalPending = pending.stream().mapToDouble(PendingFinesDto::getAmount).sum();

        List<FinePerMeeting> fines = fineRepository.findFinePerMeeting();

        return SecretaryHomePageDto.builder().totalMeetings(meetingCount).totalFinesCollected(paidFines)
                .totalPendingFines(totalPending).latestFines(pending).meetinglyFine(fines).build();
    }

    public PendingFinesDto buildPendingFineObject(Fine fine) {
        return PendingFinesDto.builder()
                .amount(fine.getFineCategory().getChargeableAmount())
                .memberId(fine.getMemberId().getMemberId())
                .dateFined(fine.getDateEnforced())
                .fine(fine.getFineCategory().getFineName())
                .name(fine.getMemberId().getPrevRef().getFirstName() + " "
                        + fine.getMemberId().getPrevRef().getLastName())

                .build();
    }

    public FineRes addFineCategory(FineCategoryDto fineCategoryDto) {

        String fineId = fineCategoryDto.getFineName().substring(0, 3).toUpperCase()
                + utility.randomApplicationID().substring(5);

        FineCategory fineCategory = FineCategory.builder()
                .fineCategoryId(fineId)
                .chargeableAmount(fineCategoryDto.getAmount())
                .fineName(fineCategoryDto.getFineName())
                .build();

        fineCategoryRepository.save(fineCategory);

        return FineRes.builder().code(200).message("Fine added successfully").build();

    }
}
