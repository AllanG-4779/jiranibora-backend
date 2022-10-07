package org.jiranibora.com.loans;

import org.hibernate.annotations.ParamDef;
import org.jiranibora.com.loans.dto.LoanApplicationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

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
    public ResponseEntity<?> supplyApplication(@RequestBody @Valid LoanApplicationDto loanApplicationDto)
            throws Exception {

        try {
            LoanApplicationDto loanResponse = loanService.addLoan(loanApplicationDto);
            return ResponseEntity.status(200).body(loanResponse);
        } catch (UnsupportedOperationException formatException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loanApplicationDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loanApplicationDto);
        }

    }

    // Approve loan

    @PostMapping("/take/action/{loan_id}")
    public ResponseEntity<?> approveLoan(@PathVariable String loan_id, @RequestParam(required = true) String action)
            throws Exception {
        Integer result = loanService.takeAction(loan_id, action);
        HashMap<String, String> map = new LinkedHashMap<>();
        if (result == 0) {
            map.put("message", "Action executed successfully");
            map.put("code", "200");

        } else {
            map.put("message", "Action failed");
            map.put("code", "500");

        }
        map.put("action", action);

        return ResponseEntity.status(Integer.valueOf(map.get("code"))).body(map);

    }
}
