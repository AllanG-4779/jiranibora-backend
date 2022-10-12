package org.jiranibora.com.profile;

import java.util.List;
import java.util.stream.Collectors;

import org.jiranibora.com.application.Utility;
import org.jiranibora.com.contributions.ContributionRepository;
import org.jiranibora.com.contributions.MemberContributionRepository;
import org.jiranibora.com.contributions.TransactionRepository;

import org.jiranibora.com.loans.LoanStatementRepo;
import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.Fine;

import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.MemberContribution;
import org.jiranibora.com.models.Penalty;
import org.jiranibora.com.models.Transactions;
import org.jiranibora.com.payment.FineRepository;
import org.jiranibora.com.payment.PenaltyRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor

public class ProfileService {
        private TransactionRepository transactionRepository;
        private Utility utility;
        private LoanStatementRepo loanStatementRepo;
        private FineRepository fineRepository;
        private PenaltyRepository penaltyRepository;
        private MemberContributionRepository memberContributionRepository;
        private ContributionRepository contributionRepository;

        public ProfileDto getUserProfile() {

                // Authentication authentication=
                // SecurityContextHolder.getContext().getAuthentication();

                // if(!authentication.isAuthenticated() || authentication instanceof
                // AnonymousAuthenticationToken){
                // return ProfileDto.builder().accountHolder("Authentication required").build();

                // }

                Member member = utility.getAuthentication();

                if (member == null) {
                        return ProfileDto.builder().accountHolder("Not found").build();
                }
                List<Transactions> contributions = transactionRepository.findByMemberIdAndPaymentCategory(member,
                                "Contribution");
                Double loansCharged = loanStatementRepo.findAllByMemberId(member.getMemberId()).stream()
                                .mapToDouble(each -> (each.getInterest() + each.getPrinciple())).sum();

                Double sumContribution = getSum(contributions);

                List<FineDto> allUnpaidFines = fineRepository.findByPaidAndMemberId(false, member).stream()
                                .map(each -> FineDto.builder().fineCode(each.getFineId().getFineCategory())
                                                .category(each.getFineCategory().getFineName())
                                                .dateAdded(each.getDateEnforced())
                                                .meetingId(each.getMeetingId().getMeetingId()).paid(each.getPaid())
                                                .amount(each.getFineCategory().getChargeableAmount()).build())
                                .collect(Collectors.toList());

                List<PenaltyDto> allUnpaidPenalties = penaltyRepository.findByStatusAndMemberId("Pending", member)
                                .stream()
                                .map(each -> PenaltyDto.builder().penaltyCode(each.getPenCode())
                                                .meetMonth(each.getContributionId().getMonth()).amount(each.getAmount())
                                                .status(each.getStatus()).dateAdded(each.getDatePenalized()).build())
                                .collect(Collectors.toList());
                Double finePlusPenalties = 0.0;
                try {

                        finePlusPenalties = allUnpaidPenalties.stream().mapToDouble(each -> each.getAmount()).sum()
                                        + allUnpaidFines.stream().mapToDouble(each -> each.getAmount()).sum();

                } catch (NullPointerException ex) {
                        System.out.print(ex.getMessage());
                }

                AccountSummary accountSummary = AccountSummary.builder()
                                .contributions(sumContribution)
                                .fineAndPenalties(finePlusPenalties)
                                .loanAndInterest(loansCharged)
                                .build();

                return ProfileDto.builder().accountHolder(member.getFullName())
                                .accountSummary(accountSummary)
                                .fines(allUnpaidFines)
                                .penalties(allUnpaidPenalties)
                                .build();

        }

        public Double getSum(List<Transactions> current) {
                Double sum = 0.0;
                for (Transactions each : current) {
                        sum += each.getAmount();
                }
                return sum;
        }

        public List<Contribution> findMemberContributions() throws Exception {

                Member member = utility.getAuthentication();
                if (member == null) {
                        throw new Exception("You are not authenticated");
                }
                // Get all contributions this year
                List<Contribution> contribution = contributionRepository.findAll();

                // Get All contributionmade by a member
                List<MemberContribution> memberContributions = memberContributionRepository.findByMemberId(member);
                // compate the two list and return the one's he/she has not contributed
                for (MemberContribution memberCont : memberContributions) {
                        contribution.removeIf(
                                        each -> each.getContId().equals(memberCont.getContributionId().getContId()));
                }

                return contribution;

        }
}
