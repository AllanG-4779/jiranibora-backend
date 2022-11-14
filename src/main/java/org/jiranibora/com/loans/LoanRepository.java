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

    @Query("SELECT loans from LoanApplication loans WHERE loans.memberId=?1 AND loans.status=?2")
    List<LoanApplication> findTotalLoanDisbursedToMember(Member member, String status);

    List<LoanApplication> findByViewed(Boolean viewed);

    List<LoanApplication> findByStatusAndMemberId(String status, Member member);

    List<LoanApplication> findAllByStatus(String status);
    List<LoanApplication> findAllByMemberId(Member member);

}
