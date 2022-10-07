package org.jiranibora.com.contributions;

import org.jiranibora.com.models.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<Contribution, Integer> {
   Contribution findByStatus(String status);

   Contribution findByContId(String contId);
}
