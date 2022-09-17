package org.jiranibora.com.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
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
         try{
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              authRequest.getMemberId(),authRequest.getPassword()
             ));

         }catch(BadCredentialsException e){
             throw new Exception("Wrong username or password");
         }
       AppUser appUser = (AppUser) appUserService.loadUserByUsername(authRequest.getMemberId());

       final String token = jwt.generateToken(appUser);

       return AuthenticationResponse.builder()
               .role(appUser.getRole())
                 .access_token(token).build();

    }



}
