package org.jiranibora.com.member;

import java.util.List;
import java.util.stream.Collectors;

import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.MemberContributionRepository;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.loans.LoanRepository;
import org.jiranibora.com.loans.LoanStatementRepo;
import org.jiranibora.com.meetings.MeetingRepository;
import org.jiranibora.com.member.dto.*;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.MemberContribution;
import org.jiranibora.com.payment.FineRepository;
import org.jiranibora.com.treasurer.TreasurerService;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberService {
    private final MeetingRepository meetingRepository;
    private final AuthenticationRepository authenticationRepository;
    private final FineRepository fineRepository;

    private MemberContributionRepository contributionRepository;
    private TransactionRepository transactionRepository;
    private LoanRepository loanRepository;
    private LoanStatementRepo loanStatementRepo;
    private TreasurerService treasurerService;
    private Utility utility;


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

    public MemberEarningDto getMemberStatement(){
     Member member = utility.getAuthentication();
     List<Fines> finesList = fineRepository.findAllByMemberId(member).stream().map(each->Fines.builder()
             .fineCategory(each.getFineCategory().getFineName())
             .amount(each.getFineCategory()
                     .getChargeableAmount()).status(each.getPaid())
             .meetingId(each.getMeetingId().getMeetingId()).build()).collect(Collectors.toList());
     List<Contributions> memberConts  = contributionRepository.findByMemberId(member).stream().map(each->
                      Contributions.builder().contributionId(each.getContributionId().getContId())
                     .contributionMonth(each.getContributionId().getMonth())
                     .penalty(each.getStatus().equals("LATE")?Double.parseDouble(each.getMemberId().getPrevRef().getAmount()) *.2:0.0)
                     .build()).collect(Collectors.toList());
     List<Loans> loans = loanRepository.findByStatusAndMemberId("Approved", member).stream().map(each->
             Loans.builder()
                     .dateApproved(each.dateViewed)
                     .interestAccured(loanStatementRepo.findByApplicationId(each).getInterest() )
                     .pendingAmount(loanStatementRepo.findByApplicationId(each).getPrinciple())
                     .principalAmount(each.getAmount().doubleValue())

                     .build()
             ).collect(Collectors.toList());

     Summary summary = Summary.builder()
             .interestEarned(treasurerService.buildMemberEarningDto(member).getInterestEarned())
             .totalContributions(treasurerService.buildMemberEarningDto(member).getTotalContribution())
             .totalDeductions(treasurerService.buildMemberEarningDto(member).getTotalContribution()-treasurerService.buildMemberEarningDto(member).getNetContribution())
             .netEarning(treasurerService.buildMemberEarningDto(member).getFinalPayout())
             .build();
     return MemberEarningDto.builder().summary(summary)
             .loans(loans).fines(finesList).contributions(memberConts).build();

    }

}
