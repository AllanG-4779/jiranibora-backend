package org.jiranibora.com.fine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineCategoryDto {
    
    private String fineName;
    private Double amount;
}
