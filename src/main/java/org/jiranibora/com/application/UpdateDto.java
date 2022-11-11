package org.jiranibora.com.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateDto {
    private String param;
    private String memberId;
    private String value;
}
