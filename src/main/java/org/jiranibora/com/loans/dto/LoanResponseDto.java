package org.jiranibora.com.loans.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class LoanResponseDto {
 
    private String loanId;
    private LocalDateTime dateApplied;
    private LocalDateTime dateApproved;
    private Double amount;
    private Integer initialDuration;
    private Double initialInterest;
    private Double extraInterest;
    private String status;
    

}