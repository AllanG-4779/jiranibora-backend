package org.jiranibora.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

/**
 * Hello world!
 *
 */
@SpringBootApplication

public class JiraniBoraApplication {
    @PostConstruct
    public void initTwilio() {
        // Twilio.init();
    }

    public static void main(String[] args) {
        SpringApplication.run(JiraniBoraApplication.class, args);
    }

    // This method will update all the accounts that needs to be updated
    
}
//