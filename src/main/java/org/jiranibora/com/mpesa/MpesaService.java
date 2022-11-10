package org.jiranibora.com.mpesa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.jiranibora.com.application.Utility;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;


@Service
@AllArgsConstructor
public class MpesaService {

    private final RestTemplate restTemplate;
    private MpesaConfig mpesaConfig;



    public String getAccessToken(){
        HttpHeaders header = new HttpHeaders();
        header.setBasicAuth(mpesaConfig.getConsumerKey(), mpesaConfig.getConsumerSecret());
        HttpEntity<TokenDto> entity = new HttpEntity<>(header);

       ResponseEntity<TokenDto> response = restTemplate.exchange("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials",
               HttpMethod.GET, entity, TokenDto.class);

       if(response.getStatusCode()==HttpStatus.OK){
           return Objects.requireNonNull(response.getBody()).getAccess_token();
       }else{
           return null;
       }

    }

    public HttpStatus sendMoney(String amount, String sender) throws JsonProcessingException {
        int businessShortCode = 174379;
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = getAccessToken();
        if(accessToken == null){

            System.out.println("access token is null");
            throw new IllegalStateException("Could not fetch access token from Safaricom");
        }
        else{
            System.out.println(accessToken);
        }
        httpHeaders.setBearerAuth(getAccessToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = dateFormat.format(new Date());
        String password = String.valueOf(Base64.getEncoder()
                .encodeToString((businessShortCode+mpesaConfig.getPassKey()+timeStamp).getBytes()));
        STKPushRequest pushRequest = STKPushRequest.builder()
                .AccountReference("JiraniBora SACCO")
                .Amount(amount)
                .BusinessShortCode(businessShortCode)
                .CallBackURL("https://jiranibora.herokuapp.com/validation")
                .PartyA(sender)
                .PartyB(businessShortCode)
                .Password(password)
                .PhoneNumber(sender)
                .Timestamp(timeStamp)
                .TransactionType("CustomerPayBillOnline")
                .TransactionDesc("Description")
                .build();
        HttpEntity<?> request = new HttpEntity<>(new ObjectMapper()
                .writeValueAsString(pushRequest), httpHeaders);


       ResponseEntity<?>response = restTemplate.exchange("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest", HttpMethod.POST, request, String.class);

       if(response.getStatusCode().is2xxSuccessful()){
           return  response.getStatusCode();
       }
       else{
           return null;
       }

    }
}
