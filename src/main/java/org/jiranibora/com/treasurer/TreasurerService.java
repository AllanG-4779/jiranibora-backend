package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.MemberContributionRepository;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.loans.LoanRepository;
import org.jiranibora.com.loans.LoanStatementRepo;
import org.jiranibora.com.models.*;
import org.jiranibora.com.payment.FineRepository;
import org.jiranibora.com.payment.PenaltyRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TreasurerService {
        private final TransactionRepository transactionRepository;
        private final LoanRepository loanRepository;
        private final LoanStatementRepo loanStatementRepo;
        private final PenaltyRepository penaltyRepository;
        private final MemberContributionRepository contributionRepository;
        private final AuthenticationRepository authenticationRepository;
        private final MemberContributionRepository memberContributionRepository;
        private final FineRepository fineRepository;

        public TreasurerHomeDto getAccountStatus() {
                // Get total Contributions;
                Double totalContributions = contributionRepository.findAll().stream()
                                .mapToDouble(each -> Double.parseDouble(each.getMemberId().getPrevRef().getAmount()))
                                .sum();

                Double totalLoans = loanRepository.findAll().stream().filter(each -> each.status.equals("Approved"))
                                .mapToDouble(LoanApplication::getAmount).sum();

                double amountRecovered = transactionRepository.findAllByPaymentCategory("Loan Repayment").stream()
                                .mapToDouble(Transactions::getAmount).sum();

                Double amountInWaiting = loanStatementRepo.findAll().stream().filter(each -> each.getPrinciple() > 0)
                                .mapToDouble(LoanStatement::getPrinciple).sum();

                Double interestEarned = amountRecovered - (totalLoans - amountInWaiting);

                Double earningFromPenalties = penaltyRepository.findAll().stream().filter(each -> each.getStatus()
                                .equals("Paid")).mapToDouble(Penalty::getAmount).sum();

                return TreasurerHomeDto.builder().amountLoaned(totalLoans).amountPaid(totalLoans - amountInWaiting)

                                .interestEarned(interestEarned).earningFromPenalties(earningFromPenalties)
                                .totalContributions(totalContributions)

                                .numberOfLoansGiven(Math.toIntExact((loanStatementRepo.count())))

                                .numberOfLoansOutstanding(Math.toIntExact(loanStatementRepo.findAll().stream()

                                                .filter(each -> (each.getPrinciple() + each.getInterest()) > 0)
                                                .count()))

                                .pendingLoans(loanStatementRepo.findAll().stream()

                                                .filter(each -> (each.getPrinciple() + each.getInterest()) > 0)
                                                .map(statement -> PendingLoansDto.builder()

                                                                .amount(statement.getPrinciple())

                                                                .interest(statement.getInterest())

                                                                .dueDate(statement.getExpectedOn())

                                                                .memberId(statement.getLoanId().getMemberId()
                                                                                .getMemberId())

                                                                .name(statement.getLoanId().getMemberId().getPrevRef()

                                                                                .getFirstName()
                                                                                + " "
                                                                                + statement.getLoanId().getMemberId()
                                                                                                .getPrevRef()
                                                                                                .getLastName())

                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();
        }
        // Get the member annual earning

        public List<MemberEarningDto> getMemberEarnings() {
                List<Member> members = authenticationRepository.findAll();

                return members.stream().map(each -> buildMemberEarningDto(each)).collect(Collectors.toList());

        }

        public MemberEarningDto buildMemberEarningDto(Member member) {

                Double totalJiraniBoraContribution = getSharableIncome().getContributionsAfter();

                Double sharableIncome = getSharableIncome().getEarningAfter();

                Double totalShares = totalJiraniBoraContribution / 500;

                Double loans = loanStatementRepo.findAllByMemberId(member.getMemberId()).stream()
                                .mapToDouble(each -> (each.getInterest() + each.getPrinciple())).sum();
                Double fines = fineRepository.findByPaidAndMemberId(false, member).stream()
                                .mapToDouble(each -> each.getFineCategory().getChargeableAmount()).sum();
                Double penalties = penaltyRepository.findByStatusAndMemberId("Pending", member).stream()
                                .mapToDouble(Penalty::getAmount).sum();
                Double totalContribution = memberContributionRepository.findByMemberId(member).stream()
                                .mapToDouble(each -> Double.parseDouble(each.getMemberId().getPrevRef().getAmount())).sum();

                Double totalDeductions = (fines + penalties + loans);

                Double netContribution = totalContribution - totalDeductions;

                Double netShares = netContribution > 0 ? netContribution / 500 : 0;

                Double interestEarned = (netShares / totalShares) * sharableIncome;

                Double finalPayout = netContribution > 0 ? netContribution + interestEarned : interestEarned;

                // Log the total earnings, total shares

                DecimalFormat roundTo2dp = new DecimalFormat("0.00");
                return MemberEarningDto.builder()

                                .fines(Double.valueOf(roundTo2dp.format(fines)))
                                .penalties(Double.valueOf(roundTo2dp.format(penalties)))
                                .memberId(member.getMemberId())
                                .name(member.getFullName().split("ID")[0])
                                .totalContribution(Double.valueOf(roundTo2dp.format(totalContribution)))
                                .netContribution(netContribution)
                                .netShares(Double.valueOf(roundTo2dp.format(netShares)))
                                .loans(Double.valueOf(roundTo2dp.format(loans)))
                                .interestEarned(interestEarned)
                                .finalPayout(finalPayout)

                                .build();

        }
        // This method reduces the sharable income after deducting the loans, fines, and
        // penalties that hasn't been paid
        // up until the time of end year.

        public EarningSummary getSharableIncome() {
                Double contributionBefore = contributionRepository.findAll().stream()
                                .mapToDouble(each -> Double.parseDouble(each.getMemberId().getPrevRef().getAmount()))
                                .sum();
                Double earningBefore = getAccountStatus().getInterestEarned()
                                + getAccountStatus().getEarningFromPenalties();
                Double contributionAfter = contributionBefore;
                Double earningAfter = earningBefore;

                List<Member> members = authenticationRepository.findAll();

                for (Member member : members) {

                        contributionAfter -= eachMemberNetIncome(member);
                        earningAfter += eachMemberNetIncome(member);

                }

                return EarningSummary.builder()
                                .earningBefore(earningBefore).earningAfter(earningAfter)
                                .contributionsAfter(contributionAfter).contributionsBefore(contributionBefore)
                                .build();
        }

        public Double eachMemberNetIncome(Member member) {
                // This method is responsible for deducting all the outstanding loans, fines,
                // and penalties before calculating the final pay for a user

                Double loans = loanStatementRepo.findAllByMemberId(member.getMemberId()).stream()
                                .mapToDouble(each -> (each.getInterest() + each.getPrinciple())).sum();

                Double fines = fineRepository.findByPaidAndMemberId(false, member).stream()
                                .mapToDouble(each -> each.getFineCategory().getChargeableAmount()).sum();

                Double penalties = penaltyRepository.findByStatusAndMemberId("Pending", member).stream()
                                .mapToDouble(each -> each.getAmount()).sum();

                Double totalContribution = contributionRepository.findByMemberId(member).stream()
                                .mapToDouble(each -> Double.valueOf(each.getMemberId().getPrevRef().getAmount())).sum();

                Double deductions = loans + fines + penalties;

                Double netContribution = totalContribution - deductions;

                if (netContribution < 0) {
                        // increment the income by the total number of contributions
                        // deduct the sharabe income by the same amount;
                        return totalContribution;

                } else {
                        // increment the income by the sum of fines, penalties and loans
                        // deduct the contribution b the same amount
                        return deductions;
                }

        }

}
