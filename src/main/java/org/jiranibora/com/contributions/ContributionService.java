package org.jiranibora.com.contributions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.jiranibora.com.application.TransactionDto;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.MemberContributionDto.MemberContributionDtoBuilder;
import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.MemberContribution;
import org.jiranibora.com.models.MemberContributionPK;
import org.jiranibora.com.models.Penalty;
import org.jiranibora.com.payment.PenaltyRepository;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@EnableScheduling
@AllArgsConstructor
public class ContributionService {
    private final Utility utility;
    private final ContributionRepository contributionRepository;
    private final AuthenticationRepository authenticationRepository;
    private final MemberContributionRepository memberContributionRepository;
    private final PenaltyRepository penaltyRepository;
    private final TransactionRepository transactionRepository;
    public final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December" };

    public Integer openContribution(Integer duration) throws Exception {

        Contribution existing = contributionRepository.findByStatus("ON");

        if (Objects.nonNull(existing)) {
            return 1;
        }
        Long monthCount = (contributionRepository.count());
        if (monthCount >= 12) {
            return 2;
        }

        Contribution contribution = Contribution.builder()
                .contId("CNT" + utility.randomApplicationID().substring(6))
                .monthCount(monthCount.intValue() + 1)
                .closeOn(LocalDateTime.now().plusMinutes(duration))
                .month(months[monthCount.intValue()])
                .status("ON")
                .openOn(LocalDateTime.now())
                .build();

        try {
            contributionRepository.saveAndFlush(contribution);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return 0;
    }

    // Close the contribution
    public Boolean disableContribution(String contId) throws Exception {
        Contribution contributionToDisable = contributionRepository.findByContId(contId);
        if (!Objects.nonNull(contributionToDisable) || contributionToDisable.getStatus().equals("CLOSED")) {
            return false;
        } else {

        }
        return true;
    }

    // Member's contribution
    @Transactional(rollbackOn = Exception.class)
    public ContributionResponse memberContribution(String contributionId) throws Exception {
        ContributionResponse contRes = new ContributionResponse();
        // Is there any contribution with such ID

        Contribution currenContribution = contributionRepository.findByContId(contributionId);

        if (currenContribution == null) {
            contRes.setCode(404);
            contRes.setMessage("No Contribution was found");
            return contRes;
        }

        else {
            // Is the person authenticated, i.e is there a securityContext in place?

            Member member = utility.getAuthentication();
                    
            if (member == null) {
                contRes.setCode(403);
                contRes.setMessage("You are not authenticated");
                return contRes;

            } else {
                
                String contributionStatus;
                MemberContributionPK memberContributionPK = MemberContributionPK.builder()
                        .contributionId(contributionId)
                        .memberId(member.getMemberId())
                        .build();

                // If the member has already contributed...
                Optional<MemberContribution> alreadyContributed = memberContributionRepository
                        .findById(memberContributionPK);
                if (alreadyContributed.isPresent()) {
                    contRes.setCode(409);
                    contRes.setMessage("You have already made your contribution for this");
                    return contRes;
                }
                Integer monthlyContributionAmount = Integer.valueOf(member.getPrevRef().getAmount());

                if (currenContribution.getStatus().equals("CLOSED")) {

                    contributionStatus = "LATE";
                } else {
                    contributionStatus = "TIMELY";
                }

                // Now find the contribution

                MemberContribution memberContribution = MemberContribution.builder()
                        .memberContribution(memberContributionPK)
                        .memberId(member)
                        .contributionId(currenContribution)
                        .datedone(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .status(contributionStatus)
                        .build();

                memberContributionRepository.saveAndFlush(memberContribution);
                contRes.setCode(200);
                contRes.setMessage("Contribution made successfully");

                // Register that transaction in a transaction table;
                TransactionDto transactionDto = TransactionDto.builder()
                        .amount(Double.valueOf(monthlyContributionAmount))
                        .memberId(member)
                        .transactionDate(LocalDateTime.now())
                        .serviceId(contributionId)
                        .paymentCategory("Contribution")
                        .build();

                Boolean result = utility.addTransaction(transactionDto);
                if (result) {
                    return contRes;
                } else {
                    throw new Exception("Transaction failed ");
                }

            }
        }

    }

    public List<MemberContributionDto> getMemberContributions() throws Exception {
        Member member = utility.getAuthentication();
        if (member == null) {
            throw new Exception("You are not authenticated");
        }
        List<MemberContribution> memberCont = memberContributionRepository.findByMemberId(member);
        return memberCont.stream()
                .map(each -> buildMemberContribution(each))
                .collect(Collectors.toList());
    }

    // build member for penalties
    public Penalty getPenalties(String eachID, Contribution contributionToDisable) {
        Member latePayer = authenticationRepository.findMemberByMemberId(eachID);

        return Penalty.builder()
                .memberId(latePayer)
                .contributionId(contributionToDisable)
                .datePenalized(LocalDateTime.now())
                .penCode("PNT" + utility.randomApplicationID().substring(6))
                .status("Pending")
                .amount(Integer.valueOf(latePayer.getPrevRef().getAmount()) * 0.2)
                .build();
    }

    public MemberContributionDto buildMemberContribution(MemberContribution currenContribution) {
        // find the date from the transaction list where the member made the
        // contribution so that you can ext
        MemberContributionDtoBuilder memberContributionDto = MemberContributionDto.builder();
        try {
            LocalDateTime dateContribution = transactionRepository
                    .findByMemberIdAndPaymentCategory(currenContribution.getMemberId(),
                            "Contribution")
                    .stream()
                    .filter(each -> each.getTrxCode().split("_")[1]
                            .equals(currenContribution.getMemberContribution().getContributionId()))
                    .findFirst().get().getTransactionDate();

            memberContributionDto.date(dateContribution);

        } catch (Exception e) {

            memberContributionDto.date(LocalDateTime.now()).build();
        }

        return memberContributionDto
                .contributionId(currenContribution.getContributionId().getContId())
                .penalty(currenContribution.getStatus().equals("LATE")
                        ? Double.valueOf(currenContribution.getMemberId().getPrevRef().getAmount()) * 0.2
                        : 0.0)
                .contributionId(currenContribution.getContributionId().getContId())
                .month(currenContribution.getContributionId().getMonth())
                .status(currenContribution.getStatus()).build();

    }

    // Auto close the contribution
    @Scheduled(initialDelay = 200L, fixedDelayString = "PT1M")
    public void closeContribution() throws Exception {
        log.info("Checking the outdated contributions");
        Optional<Contribution> contributionOption = contributionRepository.findAll().stream()
                .filter(each -> each.getStatus().equals(("ON")) && LocalDateTime.now().isAfter(each.getCloseOn()))
                .findFirst();
        if (contributionOption.isPresent()) {
            Contribution contributionToDisable = contributionOption.get();

            contributionToDisable.setCloseOn(LocalDateTime.now());
            contributionToDisable.setStatus("CLOSED");
            // Add Penalties to all the member who have not contributed
            // Get all the members who has not contributed
            List<Member> allMembers = authenticationRepository.findAll();

            List<MemberContribution> allMemberContribution = memberContributionRepository
                    .findAllByContributionId(contributionToDisable);
            // Get the list of members who have contributed
            List<Member> notContributed = allMembers.stream().filter(each ->

            allMemberContribution.stream()
                    .noneMatch(
                            eachContribution -> eachContribution.getMemberId().getMemberId().equals(each.getMemberId()))

            ).collect(Collectors.toList());

            // The returned members have not contributed, fine them

            List<String> latePaymentsIds = notContributed.stream().map(eachMember -> eachMember.getMemberId())
                    .collect(Collectors.toList());

            // For each of those MemberIds, create a Penalty profile and save
            List<Penalty> latePayments = latePaymentsIds.stream()
                    .map(eachID -> getPenalties(eachID, contributionToDisable)).collect(Collectors.toList());

            penaltyRepository.saveAllAndFlush(latePayments);

            try {
                contributionRepository.saveAndFlush(contributionToDisable);

            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }

        } else {
            System.out.println("No contribution was found");
        }

    }

    // Get me all the contributions in the system has had
    public List<ContributionInfoDto> getAllContributions() {
        Collection<Contribution> allContributions = contributionRepository.findClosed("CLOSED");

        return allContributions.stream()
                .map(each -> buildContributionInfoDto(each)).collect(Collectors.toList());

        // Collection<Penalty> allPenalties = penaltyRepository.findByContributionId();

    }

    public ContributionInfoDto buildContributionInfoDto(Contribution contribution) {

        Integer lockedMembers = penaltyRepository.findByContributionId(contribution).size();
        Double expectedPenalties = penaltyRepository.findByContributionId(contribution).stream()
                .mapToDouble(each -> each.getAmount()).sum();
        List<MemberContribution> allMemberCont = memberContributionRepository.findAllByContributionId(contribution);
        Double amountColleted = allMemberCont.stream()
                .mapToDouble(each -> Double.valueOf(each.getMemberId().getPrevRef().getAmount())).sum();

        return ContributionInfoDto.builder()
                .contributionId(contribution.getMonth())
                .amountCollected(amountColleted)
                .lockedMembers(lockedMembers)
                .expectedPenalties(expectedPenalties)
                .closeDate(contribution.getCloseOn())
                .openDate(contribution.getOpenOn())
                .build();

    }

}
