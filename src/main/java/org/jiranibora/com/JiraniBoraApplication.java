package org.jiranibora.com;

import com.twilio.Twilio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class JiraniBoraApplication
{
    @PostConstruct
    public void initTwilio(){
       // Twilio.init();
    }
    public static void main( String[] args )
    {
        SpringApplication.run(JiraniBoraApplication.class, args);
    }
}
