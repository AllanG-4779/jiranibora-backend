package org.jiranibora.com.payment;

import java.util.List;

import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    Penalty findByPenCode(String code);
    List<Penalty> findByStatusAndMemberId(String status, Member memberId);

}