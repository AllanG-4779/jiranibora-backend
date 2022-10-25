package org.jiranibora.com.mpesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TokenDto {
    private Integer expires_in;
    private String access_token;

}
