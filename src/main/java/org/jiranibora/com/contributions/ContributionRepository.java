package org.jiranibora.com.contributions;

import java.util.Collection;

import org.jiranibora.com.models.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContributionRepository extends JpaRepository<Contribution, Integer> {
   Contribution findByStatus(String status);

   Contribution findByContId(String contId);
   
   @Query("SELECT contributions from Contribution contributions where contributions.status=?1")
   Collection<Contribution> findClosed(String status);
}
