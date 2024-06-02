package com.tera.pretest.context.cpumonitoring.factory;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByDayBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByHourBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByMinuteBackup;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;

import java.util.List;
import java.util.stream.Collectors;


public class BuildFactory {
    private static final BuildFactory instance = new BuildFactory();

    public  CpuUsageRateByDayBackup toBuildByDayStats(CpuUsageRateByDay backupData){
        return CpuUsageRateByDayBackup.builder()
                .cpuRateByDaySeq(backupData.getCpuRateByDaySeq())
                .average(backupData.getAverage())
                .maximumUsage(backupData.getMaximumUsage())
                .minimumUsage(backupData.getMinimumUsage())
                .createTime(backupData.getCreateTime())
                .build();
    }

    public  List<CpuUsageRateByDayBackup> toBackupDataByDayStats(List<CpuUsageRateByDay> backupData){
        return backupData.stream()
                .map(data -> instance.toBuildByDayStats(data))
                .collect(Collectors.toList());
    }

    public  CpuUsageRateByHourBackup toBuildByHourStats(CpuUsageRateByHour backupData){
        return CpuUsageRateByHourBackup.builder()
                .cpuRateByHourSeq(backupData.getCpuRateByHourSeq())
                .average(backupData.getAverage())
                .maximumUsage(backupData.getMaximumUsage())
                .minimumUsage(backupData.getMinimumUsage())
                .createTime(backupData.getCreateTime())
                .build();
    }

    public  List<CpuUsageRateByHourBackup> toBackupDataByHourStats(List<CpuUsageRateByHour> backupData) {
        return backupData.stream()
                .map(data->instance.toBuildByHourStats(data))
                .collect(Collectors.toList());
    }

    public  CpuUsageRateByMinuteBackup toBuildByMinuteStats(CpuUsageRateByMinute backupData){
        return CpuUsageRateByMinuteBackup.builder()
                .cpuRateByMinuteSeq(backupData.getCpuRateByMinuteSeq())
                .usageRate(backupData.getUsageRate())
                .createTime(backupData.getCreateTime())
                .build();

    }

    public List<CpuUsageRateByMinuteBackup> toBackupDataByMinuteStats(List<CpuUsageRateByMinute> backupData){
        return backupData.stream()
                .map(data->instance.toBuildByMinuteStats(data))
                .collect(Collectors.toList());
    }

}
