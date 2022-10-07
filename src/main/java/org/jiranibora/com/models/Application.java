package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;
    @Column(unique = true, nullable = false)
    private String applicationRef;
    @Column(nullable = false, updatable = false)
    private String firstName;
    @Column(nullable = false, updatable = false)
    private String lastName;
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    @Column(unique = true, nullable = false,updatable = false)
    private String emailAddress;
    @Column(nullable = false, updatable = false)
    private String amount;
    @Column(unique = true,nullable = false, updatable = false)
    private String nationalId;
    @Column(nullable = false, updatable = false)
    private LocalDate dob;
    @Column(nullable = false)
    private String residential;
    private LocalDateTime createdAt;
    private Boolean viewed;
    private LocalDateTime actedUponAt;
    private String status;
    private String reasonIfDeclined;


}
