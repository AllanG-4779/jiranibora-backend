package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = { "*" })
@RequestMapping("/admin/treasurer")
public class TreasurerController {
    private TreasurerService treasurerService;

    @GetMapping("/home")
    public ResponseEntity<?> getAllDetails() {
        TreasurerHomeDto result = treasurerService.getAccountStatus();
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/earning")
    public ResponseEntity<?> getMemberEarnings() {
        return ResponseEntity.status(200).body(treasurerService.getMemberEarnings());
    }

    @GetMapping("/report")
    public  ResponseEntity<JBPerformanceDto> getReport(){
        JBPerformanceDto performanceDto = treasurerService.getJiraniBoraPerformance();
        return ResponseEntity.status(200).body(performanceDto);
    }

}
