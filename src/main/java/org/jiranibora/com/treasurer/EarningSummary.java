package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class EarningSummary {
    private Double contributionsBefore;

    private Double earningBefore;

    
}
