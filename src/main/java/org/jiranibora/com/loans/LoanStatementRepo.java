package org.jiranibora.com.loans;

import org.jiranibora.com.models.LoanStatement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanStatementRepo extends JpaRepository<LoanStatement, Long> {

    

}
