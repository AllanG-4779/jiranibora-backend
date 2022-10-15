package org.jiranibora.com.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SecretaryFineDto {
    
    private Double existingFine; 
    private String memberId; 
    private String memberName;
    private String meetingId;

}
