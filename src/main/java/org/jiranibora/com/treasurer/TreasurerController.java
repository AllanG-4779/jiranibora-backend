package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = { "*" })
@RequestMapping("/admin/treasurer")
public class TreasurerController {
    private TreasurerService treasurerService;

    @RequestMapping("/home")
    public ResponseEntity<?> getAllDetails() {
        TreasurerHomeDto result = treasurerService.getAccountStatus();
        return ResponseEntity.status(200).body(result);
    }

    @RequestMapping(value = "/earning", method = RequestMethod.GET)
    public ResponseEntity<?> getMemberEarnings() {
        return ResponseEntity.status(200).body(treasurerService.getMemberEarnings());
    }

}
