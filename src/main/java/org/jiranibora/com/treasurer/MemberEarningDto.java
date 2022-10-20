package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class MemberEarningDto {
    private String memberId;
    private Double totalContribution;
    private Double loans;
    private Double fines;
    private Double penalties;
    private Double netShares;    
    private String name;
    private Double interestEarned;
    private Double finalPayout;
    private Double netContribution;

}
