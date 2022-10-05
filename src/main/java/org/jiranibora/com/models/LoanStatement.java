package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoanStatement {
    @Id
    private Long statementId;
    @OneToOne
    private Member memberId;
    @OneToOne
    private LoanApplication loanId;
    private Double principle;
    private Double interest;
    private LocalDateTime updatedAt;


}
