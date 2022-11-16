package org.jiranibora.com.twilio;

import lombok.AllArgsConstructor;
import org.jiranibora.com.application.ApplicationRepository;
import org.jiranibora.com.models.Application;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.payment.FineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = {"*"})
public class SMSController {
    private final SMSending smSendingService;
    private final FineRepository fineRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping("/sms/send")
    public ResponseEntity<?> sendSMS(  @RequestParam String phone) throws Exception {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        Application member =  applicationRepository.findApplicationByPhoneNumber(phone).orElse(null);
        if(member == null){
            throw new Exception("Something went wrong");
        }
        String name = member.getFirstName();


        try{
            map.put("success", "Message sent successfully");
            smSendingService.fineReminder(phone, name);
            return ResponseEntity.status(200).body(map);

        }catch(Exception e){
            map.put("success", "Twilio number is required");
            return ResponseEntity.status(403).body(map);
        }
    }

}
