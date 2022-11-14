package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class User {
    private String memberId;
    private String fullName;
    private String role;
    private Boolean status;
}
