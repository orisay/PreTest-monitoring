package com.tera.pretest.context.cpumonitoring.entity.base;

import com.tera.pretest.core.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@ToString
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CPU_USAGE_RATE_BY_HOUR")
@Entity
public class CpuUsageRateByHour extends BaseEntity { //3달 보관 후 백업
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPU_RATE_BY_HOUR_SEQ",columnDefinition = "BIGINT")
    private Long cpuRateByHourSeq;
    @Column(name = "AVERAGE")
    private Double average;
    @Column(name = "MAXIMUM_USAGE")
    private Double maximumUsage;
    @Column(name = "MINIMUM_USAGE")
    private Double minimumUsage;

    public static CpuUsageRateByHour toBuild(double average, double minimumUsage,double maximumUsage){
        return CpuUsageRateByHour.builder()
                .average(average)
                .maximumUsage(maximumUsage)
                .minimumUsage(minimumUsage)
                .build();

    }
}
