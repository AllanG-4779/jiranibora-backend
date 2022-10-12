
package org.jiranibora.com.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AccountSummary {
    private Double contributions;
    private Double loanAndInterest;
    private Double fineAndPenalties;
}
