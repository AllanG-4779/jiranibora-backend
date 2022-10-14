package org.jiranibora.com.profile;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class TransactionResDto {
    private String transactionId;
    private String transactioncategory;
    private String transactionDate;
    private Double transactionAmount;
    
}
