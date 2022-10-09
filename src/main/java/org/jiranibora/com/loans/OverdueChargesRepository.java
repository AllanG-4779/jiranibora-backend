package org.jiranibora.com.loans;

import org.assertj.core.api.LongAdderAssert;
import org.jiranibora.com.models.OverdueCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OverdueChargesRepository extends JpaRepository<OverdueCharges, Long> {

}
