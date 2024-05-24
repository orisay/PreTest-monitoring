package com.tera.pretest.context.cpumonitoring.entity.base;

import com.tera.pretest.core.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Schema(description = "CpuUsageRateByMinute")
@ToString
@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CPU_USAGE_RATE_BY_MINUTE")
@Entity
public class CpuUsageRateByMinute extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPU_RATE_BY_MINUTE_SEQ", columnDefinition = "BIGINT")
    @Schema(description = "Sequence or Auto Increment", type = "Long")
    private Long cpuRateByMinuteSeq;

    @Column(name = "USAGE_RATE")
    @Schema(description = "CPU One Minute Average Usage Rate", example = "00.00")
    private Double usageRate;

    public static CpuUsageRateByMinute toBuild(Double averageUsage) {
        return CpuUsageRateByMinute.builder()
                .usageRate(averageUsage)
                .build();
    }
}
