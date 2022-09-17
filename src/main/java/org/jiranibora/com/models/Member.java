package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Member {
    @Id
    private String memberId;

@OneToOne(targetEntity = Application.class,
        fetch = FetchType.EAGER
        )
    private Application prevRef;
    private String password;
    private Boolean isActive;
    private Boolean isEnabled;
    private String role;
}
