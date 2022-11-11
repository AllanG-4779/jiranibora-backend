package org.jiranibora.com.application;

import org.jiranibora.com.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Application findApplicationByApplicationRef(String applicationRef);


    Optional<Application> findApplicationByEmailAddress(String email);
    Optional<Application> findApplicationByPhoneNumber(String phone);
    Optional<Application> findApplicationByNationalId(String nationalId);
}
