package org.jiranibora.com.fine;

import java.util.List;

import org.jiranibora.com.models.FineCategory;
import org.jiranibora.com.payment.FineCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.twilio.http.Response;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = { "*" })
@AllArgsConstructor
public class FineController {
    private final FineService fineService;
    private FineCategoryRepository fineCategoryRepository;

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

    // add a fine category
    @PostMapping("/fine-category/add")
    public ResponseEntity<?> addCategory(@RequestBody FineCategoryDto fineCategory) {
        FineRes fineResponse = fineService.addFineCategory(fineCategory);
        return ResponseEntity.status(fineResponse.getCode()).body(fineResponse);

    }

    @GetMapping("/fine-category/get")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.status(200).body(fineCategoryRepository.findAll().stream()
                .map(e -> FineCategoryDto.builder().fineName(e.getFineName()).amount(e.getChargeableAmount()).build()));
    }

}
