package org.jiranibora.com.loans.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class MemberLoanProfileDto {
    private LoanSummaryDto loanSummary;
    private List<LoanResponseDto> loanResponseList;

}
