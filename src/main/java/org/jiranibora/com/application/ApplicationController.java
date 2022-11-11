package org.jiranibora.com.application;

import lombok.AllArgsConstructor;
import org.jiranibora.com.models.Application;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"*"})
@AllArgsConstructor
@RequestMapping("/application")

public class ApplicationController {
    private ApplicationService applicationService;
    private ApplicationRepository applicationRepository;
    @PostMapping("/apply")
    public ResponseEntity<?> saveApplication(@Valid @RequestBody  ApplicationRequest applicationRequest){
//       check if email exist
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        if(applicationRepository.findApplicationByEmailAddress(applicationRequest.getEmailAddress()).isPresent()){
           response.put("message", "Email is taken, try another one");
            return ResponseEntity.status(611).body(response);
        }
//        check if phone exists
        if(applicationRepository.findApplicationByPhoneNumber(applicationRequest.getPhoneNumber()).isPresent()){
            response.put("message", "Phone number is taken, try another one");
            return ResponseEntity.status(611).body(response);
        }
//        check if national Id exists
        if(applicationRepository.findApplicationByNationalId(applicationRequest.getNationalId()).isPresent()){
            response.put("message", "National ID is already registered, try another one");
            return ResponseEntity.status(611).body(response);
        }
        ApplicationResponse applicationRes = applicationService.addApplication(applicationRequest);
         return ResponseEntity.status(201).body(applicationRes);
    }

//    approve a member

    @PostMapping("/exec/{memberId}")
    public ResponseEntity<?> approveMember(@PathVariable String memberId,
                                           @RequestParam String action, @RequestBody Optional<Reason> reason) throws Exception{

        Map<String, String> map = new LinkedHashMap<>();
        if(applicationRepository.findApplicationByApplicationRef(memberId).getViewed()){
            return ResponseEntity.status(401).body(map.put("messsage", "The application has already been acted upon"));
        }
// The third parameter is used to set the reason if any for declining
        String reason2 = "";
        if(reason.isPresent()) reason2 = reason.get().getReason();
        Boolean status = applicationService.takeAction(memberId, action,reason2);


        if(status){
            map.put("approve", "successful");
            return ResponseEntity.status(200).body(map);
        }
        return ResponseEntity.status(403).body(map.put("approve", "failed"));

    }
    @GetMapping("/all")
    public ResponseEntity<List<Application>> getApplications(){
        List<Application> applications = applicationService.getApplications();
        return ResponseEntity.status(200).body(applications);
    }
   @GetMapping("/all/{ref}")
    public  ResponseEntity<Application> getApplication(@PathVariable String ref){

        Application application = applicationService.getApplication(Integer.parseInt(ref));
        return ResponseEntity.status(200).body(application);
   }
}
