package org.jiranibora.com.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Loans {
    private LocalDateTime dateApproved;
    private Double principalAmount;
    private Double pendingAmount;
    private Double pendingInterests;
}
