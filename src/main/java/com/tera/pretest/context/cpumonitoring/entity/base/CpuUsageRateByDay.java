package com.tera.pretest.context.cpumonitoring.entity.base;

import com.tera.pretest.core.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;


@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CPU_USAGE_RATE_BY_DAY")
@Entity
public class CpuUsageRateByDay extends BaseEntity { //1년 보관 후 백업
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPU_RATE_BY_DAY_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByDaySeq;
    @Column(name = "USAGE")
    private Double usage;
    @Column(name = "AVERAGE")
    private Double average;
    @Column(name = "MAXIMUM_USAGE")
    private Double maximumUsage;
    @Column(name = "MINIMUM_USAGE")
    private Double minimumUsage;

}
