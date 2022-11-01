package org.jiranibora.com.auth;

import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JWT jwt;
    private final AppUserService appUserService;

    @Autowired
    public JwtFilter(JWT jwt, AppUserService appUserService) {
        this.jwt = jwt;

        this.appUserService = appUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        // get the authorization
        String authorization = request.getHeader("Authorization");
        try {
            if (authorization != null && authorization.startsWith("Bearer")) {
                token = authorization.split(" ")[1];

                log.debug("Something went wrong here");
                log.debug("Here is the token", token);
                username = jwt.getUsernameFromToken(token);

            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // In other words we are checking if there is a user in the token and that user
                // is not yet
                // authenticated
                // if so, then the user is logged in
                AppUser appUser = (AppUser) appUserService.loadUserByUsername(username);

                // Now, validate the details in the token against the actual user with the said
                // username

                if (jwt.validateToken(token, appUser)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            appUser, null,
                            appUser.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }

                // We are done now proceed with the next filter chains

            }
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            System.out.println("This is the error"+e.getMessage());
            throw new BadCredentialsException("Authentication failed");
        }
    }

}
