package org.jiranibora.com.mpesa;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;

@Configuration
@NoArgsConstructor
@Data
@AllArgsConstructor
@ConfigurationProperties(prefix="mpesa")
public class MpesaConfig {
    private String consumerKey;
    private String consumerSecret;
    private String passKey;
    
}
