package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service

@NoArgsConstructor
@Data
public class ApplicationService {
    private ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }



    public ApplicationResponse addApplication(ApplicationRequest applicationRequest){
//        Generate a random ApplicationRef
     Utility utility = new Utility();
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
}
