package org.jiranibora.com.loans;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jiranibora.com.loans.dto.LoanApplicationDto;
import org.jiranibora.com.loans.dto.LoanResponseDto;
import org.jiranibora.com.loans.dto.MemberLoanProfileDto;
import org.jiranibora.com.models.LoanApplication;
import org.jiranibora.com.models.LoanStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/loan")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    public ResponseEntity<LoanRes> supplyApplication(@RequestBody @Valid LoanApplicationDto loanApplicationDto)
            throws Exception {

        // try {
        LoanRes loanRes = loanService.addLoan(loanApplicationDto);
        // return ResponseEntity.status(200).body(loanResponse);
        // } catch (UnsupportedOperationException formatException) {
        // return ResponseEntity.status(HttpStatus.CONFLICT).body(loanApplicationDto);
        // } catch (Exception ex) {
        // return
        // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loanApplicationDto);
        // }
        return ResponseEntity.status(loanRes.getCode()).body(loanRes);

    }

    // Approve loan

    @PostMapping("/take/action/{loan_id}")
    public ResponseEntity<LoanRes> approveLoan(@PathVariable String loan_id,
            @RequestParam(required = true) String action)
            throws Exception {
        LoanRes loanRes = loanService.takeAction(loan_id, action);

        return ResponseEntity.status(loanRes.getCode()).body(loanRes);

    }
    // Repay loan

    @PostMapping("/client/repay")
    private ResponseEntity<LoanRes> repayLoan(@RequestParam(required = true) Double amount, @RequestParam(required=false) String memberId) throws JsonProcessingException {

        LoanRes loanRes = loanService.repayLoan(amount, memberId);
        return ResponseEntity.status(loanRes.getCode()).body(loanRes);

    }

    // Give me the loans for a particular user
    @GetMapping("/client/all")
    public ResponseEntity<?> getAllLoans() {
        MemberLoanProfileDto userStatement = loanService.getAllStatementsforUser();
        return ResponseEntity.status(200).body(userStatement);
    }
    @GetMapping("/all/pending")
        public ResponseEntity<?> getAllPendingApplications(){
       List<LoanApplicationDto> newApplications =  loanService.findAllNewApplications();
        return ResponseEntity.status(200).body(newApplications);
    }
//    Get all loans
     @GetMapping("/loan/all")
    public  ResponseEntity<?> getAllTimeLoans(){
        List<LoanResponseDto> listOfLoans = loanService.getAllLoans();
        return ResponseEntity.status(200).body(listOfLoans);
     }



}
