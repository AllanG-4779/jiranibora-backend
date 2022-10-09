package org.jiranibora.com.loans;

import java.util.List;

import org.jiranibora.com.models.LoanStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanStatementRepo extends JpaRepository<LoanStatement, Long> {

    @Query("SELECT  statement FROM LoanStatement statement WHERE statement.loanId.memberId.memberId=?1 ")
    List<LoanStatement> findAllByMemberId(String memberId);

}
