package org.jiranibora.com.models;

import java.io.Serializable;

import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverDueChargesPK implements Serializable {

    private String loanId;
    private String clientId;

}
