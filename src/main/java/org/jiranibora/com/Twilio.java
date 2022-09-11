package org.jiranibora.com;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Twilio {
    private String accountSid;
    private String authToken;
    private String trialNumber;

}
