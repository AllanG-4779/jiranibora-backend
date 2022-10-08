package org.jiranibora.com.contributions;

import org.jiranibora.com.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
    

}
