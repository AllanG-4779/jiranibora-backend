package org.jiranibora.com.profile;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class FineDto {
    private String fineCode;
    private String category;
    private Double amount;
    private String meetingId;
    private LocalDateTime dateAdded;
    private Boolean paid;

}
