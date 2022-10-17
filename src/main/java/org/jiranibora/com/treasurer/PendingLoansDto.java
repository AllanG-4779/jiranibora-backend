package org.jiranibora.com.treasurer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class PendingLoansDto {
    private String name;
    private String memberId;
    private Double amount;
    private Double interest;
    private LocalDateTime dueDate;

}
