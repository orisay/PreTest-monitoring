package com.tera.pretest.context.cpumonitoring.entity.backup;

import com.tera.pretest.core.entity.LogBaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@ToString
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BAK_CPU_USAGE_RATE_BY_MINUTE")
@Entity
public class CpuUsageRateByMinuteBackup extends LogBaseEntity {

    @Id
    @Column(name = "CPU_RATE_BY_MINUTE_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByMinuteSeq;

    @Column(name = "USAGE_RATE")
    private Double usageRate;

}
