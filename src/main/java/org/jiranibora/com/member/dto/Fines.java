package org.jiranibora.com.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Fines {
    private String meetingId;
    private String fineCategory;
    private Double amount;
    private Boolean status;
}
