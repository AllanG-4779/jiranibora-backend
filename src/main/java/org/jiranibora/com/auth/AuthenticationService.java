package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import org.jiranibora.com.application.Utility;
import org.jiranibora.com.models.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public Boolean assignRole(String role , String memberId){
//        The existing role
        Member currentRoleHolder = authenticationRepository.findByRoleContaining(role);
        currentRoleHolder.setRole("User");
        authenticationRepository.saveAndFlush(currentRoleHolder);
//        update the role of the new user
        Member newRoleHolder = authenticationRepository.findMemberByMemberId(memberId);
        newRoleHolder.setRole("User;"+role);
        authenticationRepository.saveAndFlush(newRoleHolder);

        return true;

    }
   public Boolean activateDeactivate(String memberId, String action){
//        Member to be activated
       Member member = authenticationRepository.findMemberByMemberId(memberId);
       if(action.equals("activate")){
           member.setIsActive(true);
           authenticationRepository.saveAndFlush(member);
       }else if (action.equals("deactivate")){
           member.setIsActive(false);
           authenticationRepository.saveAndFlush(member);
       }
        return true;
   }
//   Return all active members;
    public List<User> getAllMembersByActive(){
        List<Member> allActiveMembers = authenticationRepository.findAll();
        return allActiveMembers.stream().map(each->User.builder().memberId(each.getMemberId())
                .fullName(each.getFullName()).role(each.getRole()).status(each.getIsActive()).build()).collect(Collectors.toList());

    }
}
