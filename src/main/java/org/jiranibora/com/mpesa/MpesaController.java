package org.jiranibora.com.mpesa;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MpesaController {
    private final MpesaService mpesaService;
    @GetMapping("/mpesa/get/token")
    public ResponseEntity<?> getToken() throws JsonProcessingException {
        return ResponseEntity.status(200).body(mpesaService.sendMoney("1", "254747407365"));
    }
}
