package org.jiranibora.com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinePrimaryKey implements Serializable {

    private String fineCategory;

    private String memberId;

    private String meetingId;
}
