package org.jiranibora.com.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Contributions {
    private String contributionId;
    private Double penalty;
    private String contributionMonth;

}
