package org.jiranibora.com.contributions;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

@CrossOrigin(origins = "*")
@RequestMapping("/cont")
public class ContributionController {
    private ContributionService contributionService;
    private LinkedHashMap<String, String> contributionMap;

    @Autowired
    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> startNewContribution(@RequestBody(required = true) ContributionDto contributionDto)
            throws Exception {
        contributionMap = new LinkedHashMap<>();

        Boolean result = contributionService.openContribution(contributionDto);

        if (result) {
            contributionMap.put("code", "200");
            contributionMap.put("message", "Your contribution was initiated successfully");
        } else {
            contributionMap.put("code", "409");
            contributionMap.put("message",
                    "There is an existing contribution, please end it before starting a new one");
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

        return ResponseEntity.status(Integer.valueOf(contributionMap.get("code"))).body(contributionMap);
    }

    // Member contribution endpoint
    @PostMapping("/contribute")
    public ResponseEntity<?> makeContribution(@RequestParam(required = true) String contributionId,
            @RequestParam(required = true) Double amount) throws Exception {

        ContributionResponse contributionResult = contributionService.memberContribution(contributionId, amount);

        return ResponseEntity.status(contributionResult.getCode()).body(contributionResult);

    }
}
