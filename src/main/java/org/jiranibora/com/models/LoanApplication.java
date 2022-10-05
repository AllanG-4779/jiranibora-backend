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
public class LoanApplication {
    @Id
    public String applicationId;
    @ManyToOne
    @JoinColumn(name="member_id")
    public Member memberId;
    public LocalDateTime dateApplied;
    public Integer amount;
    @Column(nullable = false)
    public Boolean owner;
    public String fullName;
    public String nationalId;
    public Boolean status;
    public Integer duration;
}
