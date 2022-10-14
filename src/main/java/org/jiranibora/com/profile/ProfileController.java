package org.jiranibora.com.profile;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.MemberContribution;
import org.jiranibora.com.models.Transactions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = { "*" })
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/user")
    public ProfileDto getProfile() {

        return profileService.getUserProfile();

    }

    @GetMapping("/contributions")
    public List<Contribution> getAllContributions() throws Exception {
        return profileService.findMemberContributions();

    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactions() {

        List<TransactionResDto> transactions = profileService.getAllTransactions();

        return ResponseEntity.status(200).body(transactions);

    }

}
