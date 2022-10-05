package org.jiranibora.com.loans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanApplicationDto {
    @NotNull(message = "Amount must be provided")
    private Integer amount;
    @NotNull(message = "Duration is required")
    private Integer duration;
    @NotNull(message = "Provide the ownership")
    private Boolean owner;
    private String fullName;
    private String nationalId;
}
