package com.tera.pretest.context.cpumonitoring.factory;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByDayBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByHourBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByMinuteBackup;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@ToString
@Component
public class BuildFactory {

    public CpuUsageRateByDay toBuildByCpuUsageRateByDay(double average, double minimumUsage, double maximumUsage) {
        return CpuUsageRateByDay.builder()
                .average(average)
                .minimumUsage(minimumUsage)
                .maximumUsage(maximumUsage)
                .build();
    }

    public CpuUsageRateByHour toBuildByCpuUsageRateByHour(double average, double minimumUsage, double maximumUsage) {
        return CpuUsageRateByHour.builder()
                .average(average)
                .maximumUsage(maximumUsage)
                .minimumUsage(minimumUsage)
                .build();
    }

    public CpuUsageRateByMinute toBuildByCpuUsageRateByMinute(Double averageUsage) {
        return CpuUsageRateByMinute.builder()
                .usageRate(averageUsage)
                .build();
    }

    public  CpuUsageRateByDayBackup toBuildByDayStats(CpuUsageRateByDay backupData){
        return CpuUsageRateByDayBackup.builder()
                .cpuRateByDaySeq(backupData.getCpuRateByDaySeq())
                .average(backupData.getAverage())
                .maximumUsage(backupData.getMaximumUsage())
                .minimumUsage(backupData.getMinimumUsage())
                .flag(backupData.getFlag())
                .createTime(backupData.getCreateTime())
                .build();
    }

    public  List<CpuUsageRateByDayBackup> toBackupDataByDayStats(List<CpuUsageRateByDay> backupData){
        return backupData.stream()
                .map(this::toBuildByDayStats)
                .collect(Collectors.toList());
    }

    public  CpuUsageRateByHourBackup toBuildByHourStats(CpuUsageRateByHour backupData){
        return CpuUsageRateByHourBackup.builder()
                .cpuRateByHourSeq(backupData.getCpuRateByHourSeq())
                .average(backupData.getAverage())
                .maximumUsage(backupData.getMaximumUsage())
                .minimumUsage(backupData.getMinimumUsage())
                .flag(backupData.getFlag())
                .createTime(backupData.getCreateTime())
                .build();
    }

    public  List<CpuUsageRateByHourBackup> toBackupDataByHourStats(List<CpuUsageRateByHour> backupData) {
        return backupData.stream()
                .map(this::toBuildByHourStats)
                .collect(Collectors.toList());
    }

    public  CpuUsageRateByMinuteBackup toBuildByMinuteStats(CpuUsageRateByMinute backupData){
        return CpuUsageRateByMinuteBackup.builder()
                .cpuRateByMinuteSeq(backupData.getCpuRateByMinuteSeq())
                .usageRate(backupData.getUsageRate())
                .createTime(backupData.getCreateTime())
                .flag(backupData.getFlag())
                .build();

    }

    public List<CpuUsageRateByMinuteBackup> toBackupDataByMinuteStats(List<CpuUsageRateByMinute> backupData){
        return backupData.stream()
                .map(this::toBuildByMinuteStats)
                .collect(Collectors.toList());
    }

}
