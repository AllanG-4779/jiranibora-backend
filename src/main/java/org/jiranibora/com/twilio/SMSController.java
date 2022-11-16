package org.jiranibora.com.twilio;

import lombok.AllArgsConstructor;
import org.jiranibora.com.models.Fine;
import org.jiranibora.com.payment.FineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@AllArgsConstructor
public class SMSController {
    private final SMSending smSendingService;
    private final FineRepository fineRepository;

    @PostMapping("/sms/send/{name}")
    public ResponseEntity<?> sendSMS( @PathVariable String name, @RequestParam String phone) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try{
            map.put("success", "Message sent successfully");
            smSendingService.fineReminder(phone, name);
            return ResponseEntity.status(200).body(map);

        }catch(Exception e){
            map.put("success", "Message sent successfully");
            return ResponseEntity.status(403).body(map);
        }
    }

}
