package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = {"*"})
@AllArgsConstructor
@RequestMapping("/application/apply")

public class ApplicationController {
    private ApplicationService applicationService;
    @PostMapping("/")
    public ResponseEntity< ApplicationResponse> saveApplication(@Valid @RequestBody  ApplicationRequest applicationRequest){

        ApplicationResponse applicationRes = applicationService.addApplication(applicationRequest);
         return ResponseEntity.status(201).body(applicationRes);
    }

}
