package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoanStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long statementId;
    @OneToOne
    @JoinColumn(name = "loan_application_id")
    private LoanApplication loanId;
    private Double principle; 
    private Double interest;
    private LocalDateTime issuedAt;
    @Formula("interest + principle")
    private Double outStanding;
}
