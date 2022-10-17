package org.jiranibora.com.fine;

import java.util.List;

import org.jiranibora.com.models.Fine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecretaryHomePageDto {
    private Long totalMeetings;
    private Double totalFinesCollected;
    private Double totalPendingFines;
    private List<PendingFinesDto> latestFines;
    private List<FinePerMeeting> meetinglyFine;

}
