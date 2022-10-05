package org.jiranibora.com.loans;


import org.jiranibora.com.loans.dto.LoanApplicationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/loan")
public class LoanController {
    private final LoanService loanService;
    @Autowired
    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }
    @PostMapping("/apply")
    public ResponseEntity<?> supplyApplication(@RequestBody @Valid LoanApplicationDto loanApplicationDto) throws Exception {

        try {
         LoanApplicationDto loanResponse =  loanService.addLoan(loanApplicationDto);
         return  ResponseEntity.status(200).body(loanResponse);
     }catch (UnsupportedOperationException formatException){
         return ResponseEntity.status(HttpStatus.CONFLICT).body(loanApplicationDto);
     }catch(Exception ex){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loanApplicationDto);
        }
    }
}
