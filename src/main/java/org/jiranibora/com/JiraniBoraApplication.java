package org.jiranibora.com;

import com.twilio.Twilio;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.jiranibora.com.twilio.TwilioConfig;


import jakarta.annotation.PostConstruct;

/**
 * Hello world!+
 *
 */
@SpringBootApplication
@AllArgsConstructor
public class JiraniBoraApplication {
//private final TwilioConfig twilioConfig;
//Initialize the Twilio Account
    // @PostConstruct
    // public void initTwilio() {
    //     Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

    // }

    public static void main(String[] args) {
        SpringApplication.run(JiraniBoraApplication.class, args);

    }

   @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
   }

    
}
//