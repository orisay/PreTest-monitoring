package com.tera.pretest.context.cpumonitoring.entity.backup;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.core.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BAK_CPU_USAGE_RATE_BY_MINUTE")
@Entity
public class CpuUsageRateByMinuteBackup extends BaseEntity { //1주일 보관 후 백업
    @Id
    @Column(name = "CPU_RATE_BY_MINUTE_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByMinuteSeq;
    @Column(name = "USAGE_RATE")
    private Double usageRate;

    public static CpuUsageRateByMinuteBackup toBuild(CpuUsageRateByMinute backupData){
        return CpuUsageRateByMinuteBackup.builder()
                .cpuRateByMinuteSeq(backupData.getCpuRateByMinuteSeq())
                .usageRate(backupData.getUsageRate())
                .createTime(backupData.getCreateTime())
                .build();

    }

    public static List<CpuUsageRateByMinuteBackup> toBackupData(List<CpuUsageRateByMinute> backupData){
        return backupData.stream()
                .map(CpuUsageRateByMinuteBackup::toBuild)
                .collect(Collectors.toList());

    }
}
