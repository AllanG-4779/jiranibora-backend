package org.jiranibora.com.penalty;

import org.jiranibora.com.models.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    Penalty findByPenCode(String code);

}