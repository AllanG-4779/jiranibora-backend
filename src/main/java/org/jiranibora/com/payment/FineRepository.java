package org.jiranibora.com.payment;

import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.FinePrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, FinePrimaryKey> {

}
