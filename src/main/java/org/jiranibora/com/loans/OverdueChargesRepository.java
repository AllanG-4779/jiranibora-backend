package org.jiranibora.com.loans;

import java.util.List;

import org.jiranibora.com.models.OverdueCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OverdueChargesRepository extends JpaRepository<OverdueCharges, Long> {
    @Query(value = "SELECT SUM(overdue_charge) FROM overdue_charges charge WHERE charge.loan_id=?1", nativeQuery = true)
    Double findInterestCharged(String loan_id);

    @Query(value = "SELECT sum(overdue_charge) from overdue_charges charge inner join loan_application application  ON charge.loan_id"
            +
            "= application.application_id where member_id=?1", nativeQuery = true)

    Double findAllTimeInterestCharged(String member_id);

}
