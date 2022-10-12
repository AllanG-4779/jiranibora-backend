package org.jiranibora.com.profile;

import java.util.List;

import org.jiranibora.com.models.Fine;
import org.jiranibora.com.models.Penalty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileDto {

    private String accountHolder;
    private AccountSummary accountSummary;
    private List<PenaltyDto> penalties;
    private List<FineDto> fines;

}
