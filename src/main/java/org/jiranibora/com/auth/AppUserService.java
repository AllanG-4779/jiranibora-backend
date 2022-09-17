package org.jiranibora.com.auth;

import lombok.extern.slf4j.Slf4j;
import org.jiranibora.com.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Slf4j

public class AppUserService implements UserDetailsService {
    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public AppUserService(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;


    }

    @Override

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
       Member member =  authenticationRepository.findMemberByMemberId(username);

        return new AppUser(member);
    }
}
