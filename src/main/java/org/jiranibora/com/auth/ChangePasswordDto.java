package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChangePasswordDto {
    private String newPassword;
    private String oldPassword;
}
