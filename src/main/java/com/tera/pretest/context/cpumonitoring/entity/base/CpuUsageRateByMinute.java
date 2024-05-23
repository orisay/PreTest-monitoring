package com.tera.pretest.context.cpumonitoring.entity.base;

import com.tera.pretest.core.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Setter
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_CPU_USAGE_RATE_BY_MINUTE")
@Entity
public class CpuUsageRateByMinute extends BaseEntity { //1주일 보관 후 백업
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPU_RATE_BY_MINUTE_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByMinuteSeq;
    @Column(name = "USAGE")
    private Double usage;
}
