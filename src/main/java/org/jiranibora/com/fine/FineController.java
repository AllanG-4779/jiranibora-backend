package org.jiranibora.com.fine;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = { "*" })
@RequiredArgsConstructor
public class FineController {
    private final FineService fineService;

    @PostMapping("/fine/apply/{meetingId}/{memberId}/{categoryId}")
    public ResponseEntity<FineRes> applyFine(@PathVariable(required = true) String meetingId,
            @PathVariable(required = true) String memberId, @PathVariable(required = true) String categoryId) {
        FineRes fineResponse = fineService.addFine(meetingId, memberId, categoryId);
        return ResponseEntity.status(fineResponse.getCode())
                .body(fineResponse);
    }

    // Get me the pending fines
    @GetMapping(value = "/fine/get/pending")
    public ResponseEntity<List<PendingFinesDto>> getMethodName() {
        List<PendingFinesDto> pendingList = fineService.getAllPending();
        return ResponseEntity.status(200).body(pendingList);
    }

    // Secretary homepage API
    @GetMapping(value = "/fine/get/info")
    public SecretaryHomePageDto getHomePageData() {
        return fineService.giveSecreataryHomepageSomeData();
    }

}
