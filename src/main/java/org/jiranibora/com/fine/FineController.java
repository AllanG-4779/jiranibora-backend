package org.jiranibora.com.fine;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
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

}
