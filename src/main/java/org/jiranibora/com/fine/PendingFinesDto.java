package org.jiranibora.com.fine;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingFinesDto {
    private String name;
    private String fine;
    private String memberId;
    private String phone;
    private String meetingId;

    private Double amount;
    private LocalDateTime dateFined;

}
