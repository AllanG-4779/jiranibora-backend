package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationResponse {
    private  String access_token;
    private String role;
    private String memberId;
    private String fullName;
    private String phone;
    private String monthlyCont;
}
