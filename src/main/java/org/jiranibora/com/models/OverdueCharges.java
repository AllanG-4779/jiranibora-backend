package org.jiranibora.com.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
