package org.jiranibora.com.contributions;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = { "*" })
@RequestMapping("/cont")
public class ContributionController {
    private ContributionService contributionService;
    private LinkedHashMap<String, String> contributionMap;
    private ContributionRepository contributionRepository;

    @PostMapping("/new")
    public ResponseEntity<?> startNewContribution(@RequestParam(required = true) Integer duration)
            throws Exception {
        contributionMap = new LinkedHashMap<>();

        Integer result = contributionService.openContribution(duration);

        if (result == 0) {
            contributionMap.put("code", "200");
            contributionMap.put("message", "Your contribution was initiated successfully");
        } else if (result == 1) {
            contributionMap.put("code", "409");
            contributionMap.put("message",
                    "There is an existing contribution, please end it before starting a new one");
        } else {
            contributionMap.put("code", "403");
            contributionMap.put("message",
                    "All the 12 contributions for the year has been exhausted");
        }
        return ResponseEntity.status(Integer.valueOf(contributionMap.get("code"))).body(contributionMap);

        // For a put operation

    }

    @PutMapping("/end")
    public ResponseEntity<?> endContribution(@RequestParam String contId) throws Exception {
        contributionMap = new LinkedHashMap<>();
        // Check that the Request parameter i.e Id is passed
        if (contId != null) {

            // find the contribution belonging to that ID
            Boolean updateResult = contributionService.disableContribution(contId);

            if (updateResult) {
                contributionMap.put("code", "201");
                contributionMap.put("message", "Contribution with ID " + contId + " was closed successfully");
            } else {
                contributionMap.put("code", "404");
                contributionMap.put("message",
                        "Contribution with ID " + contId + " was not found or is already closed");
            }

        } else {
            throw new Exception("No Contribution was found with that ID");
        }

        return ResponseEntity.status(Integer.parseInt(contributionMap.get("code"))).body(contributionMap);
    }

    // Member contribution endpoint
    @PostMapping("/contribute")
    public ResponseEntity<?> makeContribution(@RequestParam(required = true) String contributionId) throws Exception {

        ContributionResponse contributionResult = contributionService.memberContribution(contributionId);

        return ResponseEntity.status(contributionResult.getCode()).body(contributionResult);

    }

    @GetMapping("/all")
    public ResponseEntity<?> getMemberContributions() throws Exception {
        List<MemberContributionDto> memberConts = contributionService.getMemberContributions();
        return ResponseEntity.status(200).body(memberConts);
    }
    @GetMapping("/active")
    public ResponseEntity<?> getActiveContribution(){
        return ResponseEntity.status(200).body(contributionRepository.findByStatus("ON"));
    }
    @GetMapping("/howmany")
    public ResponseEntity<Long> getContributionsHeldCount(){
        return ResponseEntity.status(200).body(contributionRepository.count());
    }
    @GetMapping("/jb/all")
    public ResponseEntity<?> getAllContributionsHeld(){
        return ResponseEntity.status(200).body(contributionService.getAllContributions());
    }
}
