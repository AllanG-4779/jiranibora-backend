package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contribution {
    @Id
    private String contId;
    @Column(unique = true)
    private Integer monthCount;
    @Column(unique = true)
    private String month;
    private String status;
    private LocalDateTime openOn;
    private LocalDateTime closeOn;
}
