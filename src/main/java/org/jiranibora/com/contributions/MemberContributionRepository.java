package org.jiranibora.com.contributions;

import java.util.List;
import java.util.Optional;

import org.jiranibora.com.models.MemberContribution;
import org.jiranibora.com.models.MemberContributionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.Member;

public interface MemberContributionRepository extends JpaRepository<MemberContribution, MemberContributionPK> {

    Optional<MemberContribution> findById(MemberContributionPK contriPk);

    List<MemberContribution> findByMemberId(Member memberId);

    List<MemberContribution> findAllByContributionId(Contribution contributionId);

}
