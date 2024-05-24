package com.tera.pretest.context.cpumonitoring.entity.backup;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
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
@Table(name = "BAK_CPU_USAGE_RATE_BY_HOUR")
@Entity
public class CpuUsageRateByHourBackup extends BaseEntity { //3달 보관 후 백업
    @Id
    @Column(name = "CPU_RATE_BY_HOUR_SEQ", columnDefinition = "BIGINT")
    private Long cpuRateByHourSeq;
    @Column(name = "AVERAGE")
    private Double average;
    @Column(name = "MAXIMUM_USAGE")
    private Double maximumUsage;
    @Column(name = "MINIMUM_USAGE")
    private Double minimumUsage;

    public static CpuUsageRateByHourBackup toBuild(CpuUsageRateByHour backupData){
        return CpuUsageRateByHourBackup.builder()
                .cpuRateByHourSeq(backupData.getCpuRateByHourSeq())
                .average(backupData.getAverage())
                .maximumUsage(backupData.getMaximumUsage())
                .minimumUsage(backupData.getMinimumUsage())
                .createTime(backupData.getCreateTime())
                .build();
    }

    public static List<CpuUsageRateByHourBackup> toBackupData(List<CpuUsageRateByHour> backupData) {
        return backupData.stream().map(CpuUsageRateByHourBackup::toBuild).collect(Collectors.toList());
    }
}
