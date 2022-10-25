package org.jiranibora.com;

import lombok.AllArgsConstructor;
import org.jiranibora.com.mpesa.MpesaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


import javax.annotation.PostConstruct;

/**
 * Hello world!+
 *
 */
@SpringBootApplication
@AllArgsConstructor
public class JiraniBoraApplication {

    @PostConstruct
    public void initTwilio() {
        // Twilio.init();

    }

    public static void main(String[] args) {
        SpringApplication.run(JiraniBoraApplication.class, args);

    }

   @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
   }

    
}
//