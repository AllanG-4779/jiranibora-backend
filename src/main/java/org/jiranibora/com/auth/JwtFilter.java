package org.jiranibora.com.auth;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JWT jwt;
    private final AppUserService appUserService;
     @Autowired
    public JwtFilter(JWT jwt, AppUserService appUserService) {
        this.jwt = jwt;

         this.appUserService = appUserService;
     }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


//        get the auth token
        String username = null;
        String  authorizationToken  = null;
        try{
           authorizationToken = request.getHeader("Authorization");
        }catch(NullPointerException e){
            System.out.println("No token passed");
        }
        assert authorizationToken !=null;
        if(!authorizationToken.startsWith("Bearer")){
            throw new IllegalArgumentException("Invalid auth token");
        }
        String token  = authorizationToken.split(" ")[1];
//      extract the username
        try{
             username = jwt.getUsernameFromToken(token);
        }catch(MalformedJwtException e){
            throw new BadCredentialsException("Malformed token");
        }
//      Check whether the user is not authenticated but he/she has a valid token
        if(username !=null && SecurityContextHolder.getContext().getAuthentication() !=null){
//           Get the details of the token owner
            AppUser appUser =(AppUser) appUserService.loadUserByUsername(username);
            //Validate the token with the generated user
            if(jwt.validateToken(token, appUser)){
//                authenticate the user
                UsernamePasswordAuthenticationToken authenticator = new UsernamePasswordAuthenticationToken(
                        appUser,null, appUser.getAuthorities()
                );
                authenticator.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                  set the authentication context to Spring security context
                SecurityContextHolder.getContext().setAuthentication(authenticator);
            }

        }

   filterChain.doFilter(request, response);

    }

}
