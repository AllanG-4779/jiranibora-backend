package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import org.jiranibora.com.contributions.ContributionRepository;
import org.jiranibora.com.contributions.MemberContributionRepository;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.loans.LoanRepository;
import org.jiranibora.com.loans.LoanStatementRepo;
import org.jiranibora.com.models.*;
import org.jiranibora.com.payment.PenaltyRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TreasurerService {
    private TransactionRepository transactionRepository;
    private LoanRepository loanRepository;
    private LoanStatementRepo loanStatementRepo;
    private PenaltyRepository penaltyRepository;
    private MemberContributionRepository contributionRepository;

    public TreasurerHomeDto getAccountStatus(){
//       Get total Contributions;
        Double totalContributions = contributionRepository.findAll().stream().mapToDouble(each->
                Double.parseDouble(each.getMemberId().getPrevRef().getAmount())).sum();

        Double totalLoans = loanRepository.findAll().stream().filter(each->each.status.equals("Approved"))
                .mapToDouble(LoanApplication::getAmount).sum();

        double amountRecovered = transactionRepository.findAllByPaymentCategory("Loan Repayment").stream()
                .mapToDouble(Transactions::getAmount).sum();

        Double amountInWaiting = loanStatementRepo.findAll().stream().filter(each->each.getPrinciple()>0)
                .mapToDouble(LoanStatement::getPrinciple).sum();

        Double interestEarned = amountRecovered - (totalLoans -amountInWaiting);

        Double earningFromPenalties = penaltyRepository.findAll().stream().filter(each->each.getStatus()
                .equals("Paid")).mapToDouble(Penalty::getAmount).sum();

        return TreasurerHomeDto.builder().amountLoaned(totalLoans).amountPaid(totalLoans-amountInWaiting)

                .interestEarned(interestEarned).earningFromPenalties(earningFromPenalties).totalContributions(totalContributions)

                .numberOfLoansGiven(Math.toIntExact((loanStatementRepo.count())))

                .numberOfLoansOutstanding(Math.toIntExact(loanStatementRepo.findAll().stream()

                        .filter(each -> (each.getPrinciple() + each.getInterest()) > 0).count()))

                .pendingLoans(loanStatementRepo.findAll().stream()

                        .filter(each->(each.getPrinciple()+each.getInterest())>0).map(statement->PendingLoansDto.builder()

                                .amount(statement.getPrinciple())

                                .interest(statement.getInterest())

                                .dueDate(statement.getExpectedOn())

                                .memberId(statement.getLoanId().getMemberId().getMemberId())

                                .name(statement.getLoanId().getMemberId().getPrevRef()

                                        .getFirstName()+" "+statement.getLoanId().getMemberId().getPrevRef().getLastName())

                                .build()).collect(Collectors.toList()))
                .build();
    }

}
