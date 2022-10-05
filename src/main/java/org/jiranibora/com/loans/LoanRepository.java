package org.jiranibora.com.loans;

import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, String> {
    @Query("SELECT loan FROM LoanApplication  loan WHERE  loan.memberId=?1 AND loan.status=false")
     LoanApplication findByMemberId(Member member);
}
