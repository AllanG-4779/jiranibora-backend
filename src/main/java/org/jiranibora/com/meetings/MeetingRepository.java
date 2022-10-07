
package org.jiranibora.com.meetings;

import org.jiranibora.com.models.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, String> {

    Meeting findByStatus(String status);
  
}
