package com.tera.pretest.context.cpumonitoring.entity.backup;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
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

    public static CpuUsageRateByDayBackup toBuild(CpuUsageRateByDay backupData){
        return CpuUsageRateByDayBackup.builder()
                .cpuRateByDaySeq(backupData.getCpuRateByDaySeq())
                .average(backupData.getAverage())
                .maximumUsage(backupData.getMaximumUsage())
                .minimumUsage(backupData.getMinimumUsage())
                .createTime(backupData.getCreateTime())
                .build();
    }

    public static List<CpuUsageRateByDayBackup> toBackupData(List<CpuUsageRateByDay> backupData){
        return backupData.stream().map(CpuUsageRateByDayBackup::toBuild).collect(Collectors.toList());


    }

}
