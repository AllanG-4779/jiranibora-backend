package org.jiranibora.com.payment;

import org.jiranibora.com.models.FineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineCategoryRepository extends JpaRepository<FineCategory, String> {

    FineCategory findByFineName(String fineName);
}
