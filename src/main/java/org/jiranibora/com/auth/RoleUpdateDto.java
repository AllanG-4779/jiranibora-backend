package org.jiranibora.com.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoleUpdateDto {
    private String memberId;
    private String role;
}
