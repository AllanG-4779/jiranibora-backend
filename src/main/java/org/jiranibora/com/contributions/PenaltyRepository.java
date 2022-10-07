package org.jiranibora.com.contributions;

import org.jiranibora.com.models.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    
}
