package org.jiranibora.com.profile;
import java.time.LocalDateTime;

import lombok.*;
@Data
@Builder
@AllArgsConstructor
public class PenaltyDto {
    private String penaltyCode;
    private String meetMonth;
    private Double amount;
    private String status;
    private LocalDateTime dateAdded;
    
}
