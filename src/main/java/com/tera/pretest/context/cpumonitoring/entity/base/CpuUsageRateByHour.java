package com.tera.pretest.context.cpumonitoring.entity.base;

import com.tera.pretest.core.entity.LogBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Schema(name = "CpuUsageRateByHour")
@ToString(callSuper = true)
@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CPU_USAGE_RATE_BY_HOUR")
@Entity
public class CpuUsageRateByHour extends LogBaseEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CPU_RATE_BY_HOUR_SEQ", columnDefinition = "BIGINT")
    @Schema(description = "Sequence or Auto Increment", type = "integer", format = "int64")
    private Long cpuRateByHourSeq;

    @Schema(description = "CPU One Hour Average Usage Rate", example = "00.00")
    @Column(name = "AVERAGE")
    private Double average;

    @Schema(description = "CPU One Hour Maximum Usage Rate", example = "00.00")
    @Column(name = "MAXIMUM_USAGE")
    private Double maximumUsage;

    @Schema(description = "CPU One Hour Minimum Usage Rate", example = "00.00")
    @Column(name = "MINIMUM_USAGE")
    private Double minimumUsage;

}
