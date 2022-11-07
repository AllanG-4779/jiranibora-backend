package org.jiranibora.com.payment;

import java.util.List;

import org.jiranibora.com.fine.FinePerMeeting;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.FinePrimaryKey;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FineRepository extends JpaRepository<Fine, FinePrimaryKey> {

    List<Fine> findByPaidAndMemberId(Boolean paid, Member memberId);

    List<Fine> findAllByMeetingIdAndMemberId(Meeting meetingId, Member memberId);
    List<Fine> findAllByMemberId(Member member);

    List<Fine> findByPaid(Boolean paid);
    // Find list of meetings and Sums
    @Query(value = " SELECT meet.month, SUM(fc.chargeable_amount)  as total FROM fine f INNER JOIN Meeting meet ON meet.meeting_id=f.meeting_id INNER JOIN fine_category fc  ON \n" +
            "f.fine_category = fc.fine_category_id where f.paid=true GROUP BY meet.month", nativeQuery = true)
    List<FinePerMeeting> findFinePerMeeting();

    

}
