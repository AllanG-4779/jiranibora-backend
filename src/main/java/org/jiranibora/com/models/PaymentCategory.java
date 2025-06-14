package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentCategory {
    @Id
    private Integer id;
    @Column(unique = true, updatable = false)
    private String paymentId;
    @Column(unique = true, updatable = false)
    private String paymentCategoryName;
}
