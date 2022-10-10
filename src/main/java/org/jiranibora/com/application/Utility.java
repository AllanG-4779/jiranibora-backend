package org.jiranibora.com.application;

import org.jiranibora.com.auth.AuthenticationRepository;
import org.jiranibora.com.contributions.TransactionRepository;
import org.jiranibora.com.models.Member;
import org.jiranibora.com.models.Transactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;

@Component
public class Utility {
  private static final Integer LENGTH = 10;
  private TransactionRepository transactionRepository;
  private final AuthenticationRepository authenticationRepository;

  @Autowired
  public Utility(TransactionRepository tRepository, AuthenticationRepository authenticationRepository) {
    this.transactionRepository = tRepository;
    this.authenticationRepository = authenticationRepository;
  }


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

  public Boolean addTransaction(TransactionDto transactionDto) {
    Transactions transaction = Transactions.builder()
        .amount(transactionDto.getAmount())
        .trxCode("TRX" + this.randomApplicationID().substring(4) + "_" + transactionDto.getServiceId())
        .memberId(transactionDto.getMemberId())
        .paymentCategory(transactionDto.getPaymentCategory())
        .transactionDate(transactionDto.getTransactionDate())
        .build();
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

    return authenticationRepository.findMemberByMemberId(authentication.getName());

  }
}
