package org.jiranibora.com.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthenticationController {

    private  final AuthenticationManager authenticationManager;
    private final JWT jwt;
    private final AppUserService appUserService;
     @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
              JWT jwt, AppUserService appUserService) {
         this.authenticationManager = authenticationManager;

         this.jwt = jwt;
         this.appUserService = appUserService;
     }

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




}
