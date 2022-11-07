package org.jiranibora.com.loans;

import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, String> {
    @Query("SELECT loan FROM LoanApplication  loan WHERE  loan.memberId=?1 AND loan.status='Pending'")
    LoanApplication findByMemberId(Member member);

    @Query(value = "SELECT SUM(amount) from loan_application where status='Approved' AND member_id=?1", nativeQuery = true)
    Double findTotalLoanDisbursedToMember(String memberId);

    List<LoanApplication> findByViewed(Boolean viewed);

    List<LoanApplication> findByStatusAndMemberId(String status, Member member);

    List<LoanApplication> findAllByStatus(String status);
    List<LoanApplication> findAllByMemberId(Member member);

}
