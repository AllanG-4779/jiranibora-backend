package org.jiranibora.com.application;

import java.time.LocalDateTime;

import org.jiranibora.com.models.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String serviceId;
    private String paymentCategory;
    private Member memberId;
    private Integer amount;
    private LocalDateTime transactionDate;

}
