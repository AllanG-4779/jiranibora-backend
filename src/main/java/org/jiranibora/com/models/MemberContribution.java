package org.jiranibora.com.models;

import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberContribution {
    @EmbeddedId
    private MemberContributionPK memberContribution;

    @MapsId("memberId")
    @ManyToOne(targetEntity = Member.class)
    @JoinColumn(name = "member_id")
    private Member memberId;

    @ManyToOne(targetEntity = Contribution.class)
    @MapsId("contributionId")
    @JoinColumn(name = "contribution_id")
    private Contribution contributionId;
  
    private LocalDateTime datedone;

    private String status;

}
