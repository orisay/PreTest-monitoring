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
@Table(name = "BAK_CPU_USAGE_RATE_BY_DAY")
@Entity
public class CpuUsageRateByDayBackup extends BaseEntity {
    @Id
    @Column(name = "CPU_RATE_BY_DAY_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByDaySeq;
    @Column(name = "AVERAGE")
    private Double average;
    @Column(name = "MAXIMUM_USAGE")
    private Double maximumUsage;
    @Column(name = "MINIMUM_USAGE")
    private Double minimumUsage;

}
