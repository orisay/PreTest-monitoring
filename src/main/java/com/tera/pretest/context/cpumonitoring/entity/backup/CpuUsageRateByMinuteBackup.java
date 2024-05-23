package com.tera.pretest.context.cpumonitoring.entity.backup;

import com.tera.pretest.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BAK_CPU_USAGE_RATE_BY_MINUTE")
@Entity
public class CpuUsageRateByMinuteBackup extends BaseEntity { //1주일 보관 후 백업
    @Id
    @Column(name = "CPU_RATE_BY_MINUTE_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByMinuteSeq;
    @Column(name = "USAGE")
    private Double usage;
}
