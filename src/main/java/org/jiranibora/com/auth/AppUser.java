package org.jiranibora.com.auth;

import org.jiranibora.com.models.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppUser implements UserDetails {
    private final Member member;


    public AppUser(Member member){
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(member.getRole().split(";"))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    public String getFullName() {
       return  member.getPrevRef().getFirstName()
                + " " + member.getPrevRef().getLastName();
    }
    public String getRole(){
        return member.getRole();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Objects.equals(member.getPrevRef().getStatus(), "Approved");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return member.getIsActive();
    }

    @Override
    public boolean isEnabled() {
        return member.getIsEnabled();
    }
}
