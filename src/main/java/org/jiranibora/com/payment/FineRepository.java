package org.jiranibora.com.payment;

import java.util.List;

import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.FinePrimaryKey;
import org.jiranibora.com.models.Meeting;
import org.jiranibora.com.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, FinePrimaryKey> {

    List<Fine> findByPaidAndMemberId(Boolean paid, Member memberId);

    List<Fine> findAllByMeetingIdAndMemberId(Meeting meetingId, Member memberId);

    List<Fine> findByPaid(Boolean paid);

}
