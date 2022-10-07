package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Fine {

   @EmbeddedId
   private FinePrimaryKey fineId ;

   @ManyToOne
   @MapsId("fineCategory")
   @JoinColumn(name="fine_category")
    private FineCategory fineCategory;
    @ManyToOne(targetEntity = Member.class)

    @MapsId("memberId")
    @JoinColumn(name="member_id")
    private Member memberId;
    @ManyToOne(targetEntity = Meeting.class)
    @MapsId("meetingId")
    @JoinColumn(name="meeting_id")

    private Meeting meetingId;
    private LocalDateTime dateEnforced;
}
