package org.jiranibora.com.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.Email;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequest {
    @NotNull(message = "Both names are required")
    @NotEmpty(message="Some fields are empty")
    private String firstName;
    @NotNull(message = "Both names are required")
    @NotEmpty(message="Some fields are empty")
    private String lastName;
    @NotNull(message = "Phone number is required")
    @NotEmpty(message="Phone number cannot be blank")
    private String phoneNumber;
    @Email(message = "Provide a valid email address")
    @NotEmpty(message = "Email address is paramount")
    private String emailAddress;
    @NotEmpty(message="Amount must be provided")
    private String amount;
    @NotNull
    @NotEmpty
    private String nationalId;
    @NotEmpty
    @NotNull
    private String residential;

    @NotNull
    private LocalDate dob;
}
