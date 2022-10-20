package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreasurerHomeDto {
    private Double totalContributions;
    private Double amountLoaned;
    private Double amountPaid;
    private Double interestEarned;
    private Double earningFromPenalties;
    private Integer numberOfLoansGiven;
    private Integer numberOfLoansOutstanding;
    private List<PendingLoansDto>  pendingLoans;
}
