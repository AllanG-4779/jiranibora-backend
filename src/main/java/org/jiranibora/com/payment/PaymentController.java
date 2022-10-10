package org.jiranibora.com.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/pay")
@RestController
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;

    }

    @PutMapping("/penalty")
    public ResponseEntity<PaymentResponse> resolvePenalty(@RequestParam(required = true) String penaltyId) {
        PaymentResponse penaltyResponse = paymentService.resolvePenaltyService(penaltyId);
        return ResponseEntity.status(penaltyResponse.getCode()).body(penaltyResponse);
    }

    @PutMapping("/fine/{category}/{meetingId}")
    public ResponseEntity<PaymentResponse> resolveFine(@PathVariable(required = true) String meetingId,
            @PathVariable String category) {

        PaymentResponse fineResponse = paymentService.resolveFineService(category, meetingId);

        return ResponseEntity.status(fineResponse.getCode()).body(fineResponse);
    }
}
