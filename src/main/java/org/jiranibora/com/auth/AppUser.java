package org.jiranibora.com.auth;

import org.jiranibora.com.models.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AppUser implements UserDetails {
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorityList;
    private final Boolean isEnabled;
    private final String fullName;
    private final String roles;



    public AppUser(Member member){
        this.username = member.getMemberId();
        this.password = member.getPassword();
        this.fullName = member.getPrevRef().getFirstName() + " " + member.getPrevRef().getLastName();
        this.isEnabled = member.getPrevRef().getStatus().equals("Approved");
        this.authorityList = Arrays.stream(member.getRole().split(";"))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        this.roles = member.getRole();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return  username;
    }

    public String getFullName() {
       return  fullName;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
       return isEnabled;
    }

    public String getRole(){
        return roles;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
