package org.jiranibora.com.contributions;
import java.time.LocalDateTime;

import lombok.*;
@Data
@AllArgsConstructor
@Builder
public class MemberContributionDto {
    private String month;
    private String status;
    private String contributionId;
    private Double penalty;
    private LocalDateTime date;
    
}
