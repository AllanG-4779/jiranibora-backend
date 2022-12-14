package org.jiranibora.com.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.auth.PasswordEncoderConfig;
import org.jiranibora.com.models.Application;
import org.jiranibora.com.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service

@Data
@AllArgsConstructor
public class ApplicationService {
    private ApplicationRepository applicationRepository;
    private PasswordEncoderConfig passwordEncoderConfig;
    private AuthenticationRepository authenticationRepository;
    private final Utility utility;

    

    public ApplicationResponse addApplication(ApplicationRequest applicationRequest) {
        // Generate a random ApplicationRef;

    
        Application application = Application.builder()
                .applicationRef(utility.randomApplicationID())
                .amount(applicationRequest.getAmount())
                .dob(applicationRequest.getDob())
                .residential(applicationRequest.getResidential())
                .emailAddress(applicationRequest.getEmailAddress())
                .firstName(applicationRequest.getFirstName())
                .lastName(applicationRequest.getLastName())
                .nationalId(applicationRequest.getNationalId())
                .createdAt(LocalDateTime.now())
                .phoneNumber(applicationRequest.getPhoneNumber())
                .viewed(false)
                .build();
        applicationRepository.saveAndFlush(application);

        return ApplicationResponse.builder()
                .applicantId(application.getNationalId())
                .applicationRef(application.getApplicationRef())
                .time(LocalDateTime.now().toString())
                .build();

    }

    public Boolean takeAction(String applicationRef, String action, String reason) throws Exception {

        Application application = applicationRepository.findApplicationByApplicationRef(applicationRef);
        application.setActedUponAt(LocalDateTime.now());
        application.setViewed(true);
    

        if (Objects.equals(action, "approve")) {
            Member member = Member.builder()
                    .memberId("JBM" + application.getApplicationRef().substring(3))
                    .isActive(true)
                    .isEnabled(true)
                    .password(passwordEncoderConfig.passwordEncoder().encode("1234"))
                    .prevRef(application)
                    .role("USER")
                    .build();

            try {
                authenticationRepository.saveAndFlush(member);
                application.setStatus("Approved");
               
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } else if (Objects.equals(action, "disapprove")) {
            application.setStatus("Declined");
            application.setReasonIfDeclined(reason);           
        }
        try {
            applicationRepository.saveAndFlush(application);
        } catch (Exception e) {
            throw new Exception("Commiting new application failed");
        }

        // upon approving a member update the application
        return true;
    }

    // fetch applications
    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplication(int id) {
        return applicationRepository.findById(id).get();
    }
}
