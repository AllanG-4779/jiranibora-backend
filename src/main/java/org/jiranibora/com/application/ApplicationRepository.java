package org.jiranibora.com.application;

import org.jiranibora.com.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Application findApplicationByApplicationRef(String applicationRef);
}
