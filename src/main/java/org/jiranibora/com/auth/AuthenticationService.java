package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.models.Member;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationRepository authenticationRepository;
    private final Utility utility;
    private final PasswordEncoderConfig passwordEncoderConfig;
    public  String changePassword(ChangePasswordDto changePassword){
        Member member = utility.getAuthentication();
        if(member == null){
            return "Member is not authenticated";
        }
//        get the current password
        boolean currentPasswordMatch = passwordEncoderConfig.passwordEncoder()
                .matches(changePassword.getOldPassword(), member.getPassword());
        if(!currentPasswordMatch){
            return "Password not matching the old password";
        }
        else{
            member.setPassword(passwordEncoderConfig.passwordEncoder()
                    .encode(changePassword.getNewPassword()));
            authenticationRepository.saveAndFlush(member);

        }
     return "Update successful";
    }
}
