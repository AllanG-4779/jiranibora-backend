package org.jiranibora.com.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;


public class Utility {
    private static final Integer LENGTH = 10;

    public String randomApplicationID(){

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        char[] myRand = new char[LENGTH];
        for(int i = 0; i<LENGTH; i++){
          myRand[i] = chars.charAt(new Random().nextInt(chars.length()-1));
        }
       String finalString = "JBA"+ Arrays.toString(myRand);
        return finalString.replaceAll("\\[","")
                .replaceAll("]","")
                .replaceAll(" ","")
                .replaceAll(",","")
                .toUpperCase();

    }
}
