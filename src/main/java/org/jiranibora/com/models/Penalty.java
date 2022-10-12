package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Long id;
    @Column(unique = true, updatable = false)
    private String penCode;
    @ManyToOne()
    @JoinColumn(name = "contribution_id")
    private Contribution contributionId;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member memberId;
    private Double amount;
    private LocalDateTime datePenalized;
    private String status;
    
}
