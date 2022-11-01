package org.jiranibora.com.payment;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    Penalty findByPenCode(String code);

    List<Penalty> findByStatusAndMemberId(String status, Member memberId);

    Collection<Penalty> findByContributionId(Contribution contributionId);

   Optional< Penalty> findPenaltyByContributionIdAndMemberId(Contribution contribution, Member member);

}
