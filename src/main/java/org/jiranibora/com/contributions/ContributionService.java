package org.jiranibora.com.contributions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jiranibora.com.application.Utility;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.MemberContribution;
import org.jiranibora.com.models.MemberContributionPK;
import org.jiranibora.com.models.Penalty;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ContributionService {
    private final Utility utility;
    private final ContributionRepository contributionRepository;
    private final AuthenticationRepository authenticationRepository;
    private final MemberContributionRepository memberContributionRepository;
    private final PenaltyRepository penaltyRepository;

    public ContributionService(Utility utility, ContributionRepository contributionRepository,
            AuthenticationRepository authenticationRepository,
            MemberContributionRepository memberContributionRepository, PenaltyRepository penaltyRepository) {
        this.contributionRepository = contributionRepository;
        this.memberContributionRepository = memberContributionRepository;
        this.utility = utility;
        this.authenticationRepository = authenticationRepository;
        this.penaltyRepository = penaltyRepository;
    }

    public Boolean openContribution(ContributionDto contributionDto) throws Exception {

        Contribution existing = contributionRepository.findByStatus("ON");

        if (Objects.nonNull(existing)) {
            return false;
        }

        Contribution contribution = Contribution.builder()
                .contId("CNT" + utility.randomApplicationID().substring(6))
                .closeOn(LocalDateTime.now().plusMinutes(contributionDto.getDuration()))
                .month(contributionDto.getMonth())
                .status("ON")
                .openOn(LocalDateTime.now())
                .build();

        try {
            contributionRepository.saveAndFlush(contribution);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return true;
    }

    // Close the contribution
    public Boolean disableContribution(String contId) throws Exception {
        Contribution contributionToDisable = contributionRepository.findByContId(contId);
        if (!Objects.nonNull(contributionToDisable) || contributionToDisable.getStatus().equals("CLOSED")) {
            return false;
        } else {
            contributionToDisable.setCloseOn(LocalDateTime.now());
            contributionToDisable.setStatus("CLOSED");
            // Add Penalties to all the member who have not contributed
            // Get all the members who has not contributed
            List<Member> allMembers = authenticationRepository.findAll();

            List<MemberContribution> allMemberContribution = memberContributionRepository
                    .findAllByContributionId(contributionToDisable);

            List<Member> notContributed = allMembers.stream().filter(each ->

            // Get the list of members who have contributed
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

        }
        return true;
    }

    // Member's contribution
    public ContributionResponse memberContribution(String contributionId, Integer amount) {
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

            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)) {
                contRes.setCode(403);
                contRes.setMessage("You are not authenticated");
                return contRes;

            } else {
                Member member = authenticationRepository.findMemberByMemberId(authentication.getName());
                String contributionStatus;
                // If the member has already contributed...
                Optional<MemberContribution> alreadyContributed = memberContributionRepository
                        .findMemberContributionByMemberId(member);
                if (alreadyContributed.isPresent()) {
                    contRes.setCode(409);
                    contRes.setMessage("You have already made your contribution for this");
                    return contRes;
                }
                Integer monthlyContributionAmount = Integer.valueOf(member.getPrevRef().getAmount());
                if (monthlyContributionAmount < amount || monthlyContributionAmount > amount) {

                    contRes.setCode(417);
                    contRes.setMessage("Please pay only the exact amount corresponding to your monthly contribution");
                    return contRes;
                }
                // Call the payment service here

                // @To-do PAYMENT SERVICE

                if (currenContribution.getStatus() == "CLOSED") {

                    contributionStatus = "LATE";
                } else {
                    contributionStatus = "TIMELY";
                }
                // Find the authenticated user

                // Now find the contribution
                MemberContributionPK memberContributionPK = MemberContributionPK.builder()
                        .contributionId(contributionId)
                        .memberId(member.getMemberId())

                        .build();
                MemberContribution memberContribution = MemberContribution.builder()
                        .memberContribution(memberContributionPK)
                        .memberId(member)
                        .contributionId(currenContribution)
                        .status(contributionStatus)
                        .build();

                memberContributionRepository.saveAndFlush(memberContribution);
                contRes.setCode(200);
                contRes.setMessage("Contribution made successfully");
                return contRes;

            }
        }

    }

    // build member for penalties
    public Penalty getPenalties(String eachID, Contribution contributionToDisable) {
        Member latePayer = authenticationRepository.findMemberByMemberId(eachID);

        return Penalty.builder()
                .memberId(latePayer)
                .contributionId(contributionToDisable)
                .datePenalized(LocalDateTime.now())
                .penCode("PNT"+utility.randomApplicationID().substring(6))
                .status("Pending")
                .amount(Integer.valueOf(latePayer.getPrevRef().getAmount()) * 0.2)

                .build();
    }

}
