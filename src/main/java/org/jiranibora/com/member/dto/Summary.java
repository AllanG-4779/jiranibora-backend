package org.jiranibora.com.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Summary {
    private Double totalContributions;
    private Double interestEarned;
    private Double totalDeductions;
    private Double netEarning;

}
