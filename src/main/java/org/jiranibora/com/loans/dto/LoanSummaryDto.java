package org.jiranibora.com.loans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoanSummaryDto {
    private Double allTimeBorrowing;
    private Double allTimeInterest;
    private Integer declined;
}
