package org.jiranibora.com.profile;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.jiranibora.com.models.Contribution;
import org.jiranibora.com.models.MemberContribution;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin(origins={"*"})
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/user")
    public ProfileDto getProfile() {

        return profileService.getUserProfile();

    }

    @GetMapping("/contributions")
    public List<Contribution> getAllContributions() throws Exception{
        return profileService.findMemberContributions();
        
    }

}
