package org.jiranibora.com.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MemberEarningDto {
    private Summary summary;
    private List<Loans> loans;
    private List<Contributions> contributions;
    private List<Fines> fines;

}
