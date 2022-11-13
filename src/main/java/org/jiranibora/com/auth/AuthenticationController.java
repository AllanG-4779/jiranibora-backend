package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins={"*"})
public class AuthenticationController {

    private  final AuthenticationManager authenticationManager;
    private final JWT jwt;
    private final AppUserService appUserService;
    private final AuthenticationService authenticationService;


    @PostMapping("/auth/login")

    public AuthenticationResponse loginUser(@RequestBody AuthenticationRequest authRequest
                                           ) throws Exception {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getMemberId(), authRequest.getPassword()
            ));
            if (auth.isAuthenticated()) {
                AppUser appUser = (AppUser) appUserService.loadUserByUsername(authRequest.getMemberId());

                final String token = jwt.generateToken(appUser);
                log.info("Logged in as " + appUser.getAuthorities());
                return AuthenticationResponse.builder()
                        .role(appUser.getRole())
                        .access_token(token)
                        .phone(appUser.getPhone())
                        .memberId(appUser.getUsername())
                        .monthlyCont(appUser.getMonthlyContribution())
                        .fullName(appUser.getFullName())
                        .build();

            }
        } catch (BadCredentialsException e) {
            throw new Exception("Wrong username or password");
        }
        return AuthenticationResponse.builder()
                .access_token(null)
                .role("NOT authenticated")
                .build();
    }
    @PatchMapping("/password/change")
    public ResponseEntity<?> changePassword (@RequestBody ChangePasswordDto changePasswordDto){
        LinkedHashMap<String, String > map = new LinkedHashMap<>();
       String message = authenticationService.changePassword(changePasswordDto);
       map.put("message", message);

       return  ResponseEntity.status(message.startsWith("Update")?200:403).body(map);
    }





}
