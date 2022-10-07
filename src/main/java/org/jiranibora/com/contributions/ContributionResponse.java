package org.jiranibora.com.contributions;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContributionResponse {
    private Integer code;
    private String message;
}
