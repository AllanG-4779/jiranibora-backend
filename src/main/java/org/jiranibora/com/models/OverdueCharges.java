package org.jiranibora.com.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.jiranibora.com.loans.dto.LoanApplicationDto;

import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OverdueCharges {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long entryId;
    @ManyToOne
    @JoinColumn(name="loan_id")
    private LoanApplication loanId;
    private Double overdueCharge;
    private LocalDateTime lastModified;

}
