package com.tera.pretest.context.cpumonitoring.entity.base;

import com.tera.pretest.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;


@ToString
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CPU_USAGE_RATE_BY_DAY")
@Entity
public class CpuUsageRateByDay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPU_RATE_BY_DAY_SEQ", columnDefinition = "BIGINT")
    @Schema(description = "Sequence or Auto Increment", type = "Long")
    private Long cpuRateByDaySeq;

    @Schema(description = "CPU One Day Average Usage Rate", example = "00.00")
    @Column(name = "AVERAGE")
    private Double average;

    @Schema(description = "CPU One Day Maximum Usage Rate", example = "00.00")
    @Column(name = "MAXIMUM_USAGE")
    private Double maximumUsage;

    @Schema(description = "CPU One Day Minimum Usage Rate", example = "00.00")
    @Column(name = "MINIMUM_USAGE")
    private Double minimumUsage;

    public static CpuUsageRateByDay toBuild(double average, double minimumUsage, double maximumUsage) {
        return CpuUsageRateByDay.builder()
                .average(average)
                .minimumUsage(minimumUsage)
                .maximumUsage(maximumUsage)
                .build();
    }
}
