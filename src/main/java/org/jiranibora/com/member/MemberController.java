package org.jiranibora.com.member;

import org.jiranibora.com.member.dto.MemberEarningDto;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = { "*" })
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping(value = "/member/to/fine")
    public ResponseEntity<?> getMemberTofine(@RequestParam(required = true, name = "search") String searchParam) {
        SecretaryFineDto memberToFine = memberService.getMemberToBefined(searchParam);
        if (memberToFine != null) {
            return ResponseEntity.status(200).body(memberToFine);
        } else {
            return ResponseEntity.status(403).body(null);
        }
    }
    @GetMapping("/member/earning")
    public ResponseEntity<MemberEarningDto> getMemberEarning() {
        return ResponseEntity.status(200).body(memberService.getMemberStatement());
    }
}
