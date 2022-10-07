package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contribution {
    @Id
    private String contId;
    private String month;
    private String status;
    private LocalDateTime openOn;
    private LocalDateTime closeOn;
}
