package org.jiranibora.com.penalty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/penalty")
@RestController
public class PenaltyController {
    private final PenaltyService penaltyService;

    @Autowired
    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;

    }
    @PutMapping("/pay")  
    public ResponseEntity<PenaltyResponse> resolvePenalty(@RequestParam(required = true) String penaltyId) {
        PenaltyResponse penaltyResponse = penaltyService.resolvePenaltyService(penaltyId);
        return ResponseEntity.status(penaltyResponse.getCode()).body(penaltyResponse);
    }
}
