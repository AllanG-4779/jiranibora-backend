package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    @Column(unique = true, updatable = false)
    private String penCode;
    @ManyToOne(fetch = FetchType.EAGER)
    private Contribution contributionId;
    private Integer amount;
    private LocalDateTime datePenalized;
    private String status;
}
