package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Meeting {
    @Id
    private String meetingId;
    private LocalDateTime meetingDate;
    @Column(unique = true, length = 20, updatable = false)
    private String month;
    private String status;

}
