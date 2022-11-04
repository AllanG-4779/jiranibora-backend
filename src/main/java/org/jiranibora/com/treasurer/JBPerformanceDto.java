package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JBPerformanceDto {
    private Integer activeMembers;
    private Integer dormantMembers;
    private Double memberDeposits;
    private Double loanToMembers;
    private Double repaidLoans;
    private Double InterestEarned;
    private Double finesIssued;
    private Double totalPenalties;
    private Double paidPenalties;
    private Double finesPaid;
    private Integer membersWithAbsoluteNoEarning;
    private Double sharableIncome;
    private Double totalCollection;
}
