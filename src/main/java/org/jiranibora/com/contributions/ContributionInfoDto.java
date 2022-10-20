package org.jiranibora.com.contributions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ContributionInfoDto {
    private String contributionId;
    private Integer lockedMembers;
    private Double amountCollected;
    private Double expectedPenalties; 
    private LocalDateTime openDate;
    private LocalDateTime closeDate;

    
}
