package org.jiranibora.com.twilio;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import org.jiranibora.com.application.Utility;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class SMSending {
    private final TwilioConfig twilioConfig;
    private Utility utility;

    //    Contribution
//    Fine payment
//    Penalty Payment
//
    public void sendContributionSMS(String phone, double amount, String month, String name) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String time = dateTimeFormatter.format(LocalDateTime.now());
        Message message = Message.creator(
                new PhoneNumber("254" + phone), new PhoneNumber(twilioConfig.getTrialNumber()),
                String.format("Hello %s. %s Confirmed on %s at %s.\n Ksh %f has been recorded for %s Contribution payment."
                        , name, utility.randomApplicationID().substring(0, 6), time.split(" ")[0], time.split(" ")[1], amount, month
                )
        ).create();


    }

    public void loanRepayment(String phone, double amount, double outstanding, String name, String status,String expectedOn) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String time = dateTimeFormatter.format(LocalDateTime.now());
        Message message = Message.creator(
                new PhoneNumber("254" + phone), new PhoneNumber(twilioConfig.getTrialNumber()),
                String.format("Hello %s. %s Confirmed on %s at %s.\n Ksh %f has been used to %s pay" +
                                " your outstanding loan. Outstanding Loan balance is Ksh %f. To be paid before %s."
                        , name, utility.randomApplicationID().substring(0, 6), time.split(" ")[0], time.split(" ")[1],
                        amount, status,outstanding,expectedOn
                )
        ).create();
    }
    public void finePenaltyRepayment(String phone, double amount, double fineOrPen, double outStanding, String name){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String time = dateTimeFormatter.format(LocalDateTime.now());
        Message message = Message.creator(
                new PhoneNumber("254" + phone), new PhoneNumber(twilioConfig.getTrialNumber()),
                String.format("Hello %s. %s Confirmed on %s at %s.\n Ksh %f has been used to pay" +
                                " for your %s. Outstanding Fines and Penalties to Pay is Ksh %f."
                        , name, utility.randomApplicationID().substring(0, 6), time.split(" ")[0], time.split(" ")[1],
                        amount, fineOrPen,outStanding
                )
        ).create();
    }

}