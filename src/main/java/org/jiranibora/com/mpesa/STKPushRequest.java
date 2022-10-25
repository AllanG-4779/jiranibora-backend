package org.jiranibora.com.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class STKPushRequest  {
//    {
//        "BusinessShortCode": 174379,
//            "Password": "MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMjIxMDI0MjAyNTE0",
//            "Timestamp": "20221024202514",
//            "TransactionType": "CustomerPayBillOnline",
//            "Amount": 1,
//            "PartyA": 254708374149,
//            "PartyB": 174379,
//            "PhoneNumber": 254708374149,
//            "CallBackURL": "https://mydomain.com/path",
//            "AccountReference": "CompanyXLTD",
//            "TransactionDesc": "Payment of X"
//    }
    @JsonProperty("BusinessShortCode")
    private Integer BusinessShortCode;
    @JsonProperty("Password")
    private String Password;
    @JsonProperty("TransactionType")
    private String TransactionType;
    @JsonProperty("Timestamp")
    private String Timestamp;
    @JsonProperty("Amount")
    private String Amount;
    @JsonProperty("PartyA")
    private String PartyA;
    @JsonProperty("PartyB")
    private Integer PartyB;
    @JsonProperty("PhoneNumber")
    private String PhoneNumber;
    @JsonProperty("CallBackURL")
    private String CallBackURL;
    @JsonProperty("AccountReference")
    private String AccountReference;
    @JsonProperty("TransactionDesc")
    private String TransactionDesc;
}
