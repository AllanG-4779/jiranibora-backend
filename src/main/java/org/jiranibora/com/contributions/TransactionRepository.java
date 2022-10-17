package org.jiranibora.com.contributions;

import java.util.List;

import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transactions, Integer> {

    // get the total contributions for a particular member

    List<Transactions> findByMemberIdAndPaymentCategory(Member member, String paymentCategory);

    List<Transactions> findByMemberId(Member memberId);

    List<Transactions> findAllByPaymentCategory(String category);
    

}
