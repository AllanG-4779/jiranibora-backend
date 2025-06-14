package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FineCategory {
    @Id
    private String fineCategoryId;
    @Column(unique = true)
    private String fineName;
    private Double chargeableAmount;


}
