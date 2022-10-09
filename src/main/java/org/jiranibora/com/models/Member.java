package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Member {
    @Id
    private String memberId;

    @OneToOne(targetEntity = Application.class, fetch = FetchType.EAGER)
    private Application prevRef;
    private String password;
    private Boolean isActive;
    private Boolean isEnabled;
    private String role;
    @OneToMany(fetch = FetchType.EAGER)
    private List<LoanStatement> myLoans;

    public String getFullName() {
        return this.prevRef.getFirstName() + " " + this.prevRef.getLastName() + " of ID " + this.memberId;
    }

}
