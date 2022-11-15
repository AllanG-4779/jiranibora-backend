package org.jiranibora.com.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Transactions;
import org.jiranibora.com.mpesa.MpesaService;
import org.jiranibora.com.twilio.SMSending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.TransactionalException;
import java.util.Arrays;
import java.util.Random;

@Component
@AllArgsConstructor
public class Utility {
  private static final Integer LENGTH = 10;
  private TransactionRepository transactionRepository;
  private final AuthenticationRepository authenticationRepository;

  private final MpesaService mpesaService;
  private final SMSending smSending;


public String randomApplicationID() {

    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    char[] myRand = new char[LENGTH];
    for (int i = 0; i < LENGTH; i++) {
      myRand[i] = chars.charAt(new Random().nextInt(chars.length() - 1));
    }
    String finalString = "JBA" + Arrays.toString(myRand);
    return finalString.replaceAll("\\[", "")
        .replaceAll("]", "")
        .replaceAll(" ", "")
        .replaceAll(",", "")
        .toUpperCase();

  }

  public Boolean addTransaction(TransactionDto transactionDto) throws JsonProcessingException {
    // mpesa payment is added here
     HttpStatus resposestatus  = mpesaService.sendMoney(String.valueOf(transactionDto.getAmount().intValue()),
             "254"+transactionDto.getMemberId().getPrevRef().getPhoneNumber());
     if (!resposestatus.is2xxSuccessful()){
        throw new IllegalStateException("MPESA transactionnn fail");
     }
//     Send SMS


    Transactions transaction = Transactions.builder()
        .amount(transactionDto.getAmount())
        .trxCode("TRX" + this.randomApplicationID().substring(4) + "_" + transactionDto.getServiceId())
        .memberId(transactionDto.getMemberId())
        .paymentCategory(transactionDto.getPaymentCategory())
        .transactionDate(transactionDto.getTransactionDate())
        .build();
     try{
         smSending.transactionSMS(transactionDto.getMemberId().getPrevRef().getPhoneNumber()
                 ,transactionDto.getAmount(),transactionDto.getPaymentCategory(),
                 transactionDto.getMemberId().getFullName().split("ID")[0], transaction.getTrxCode());
     }catch (Exception e){
         System.out.println("SMS sending error occurred");
     }
    try {

      transactionRepository.saveAndFlush(transaction);

      return true;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }

  }

  public Member getAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    System.out.println("The user is "+ authentication.getName());

    return authenticationRepository.findMemberByMemberId(authentication.getName());

  }
}
