package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(unique = true)
    private String trxCode;
    @ManyToOne(targetEntity = PaymentCategory.class, optional = false, fetch = FetchType.LAZY)
    private PaymentCategory paymentCategory;
    @ManyToOne
    @JoinColumn(name="member_id")
    private Member memberId;
    private Integer amount;
    private LocalDateTime transactionDate;


}
