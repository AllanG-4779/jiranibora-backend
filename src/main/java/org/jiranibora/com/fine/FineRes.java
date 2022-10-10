package org.jiranibora.com.fine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class FineRes {
    private Integer code; 
    private String message;
    
}
