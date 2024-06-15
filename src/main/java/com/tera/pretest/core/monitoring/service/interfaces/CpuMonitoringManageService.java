package com.tera.pretest.core.monitoring.service.interfaces;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;

import java.util.concurrent.Future;

public interface CpuMonitoringManageService {
    void saveMonitoringCpuUsage();

    Future<Void> saveOneMinuteCpuUsageToDb();

    void saveAverageCpuUsageByHour();

    Future<Void> saveOneHourCpuUsageStatsToDb(CpuUsageRateByHour CpuUsageStat);

    void saveAverageCpuUsageByDay();

    Future<Void> saveOneDayCpuUsageStatsToDb(CpuUsageRateByDay cpuUsageStat);

    void softDeleteAndBackupCpuUsageStatsByMinute();

    void softDeleteStatsByMinute();

    void backupCpuUsageStatsByMinute();

    void softDeleteAndBackupOutdatedCpuUsageStatsByHour();

    void softDeleteStatsByHour();

    void backupCpuUsageStatsByHour();

    void softDeleteAndBackupOutdatedCpuUsageStatsByDay();

    void softDeleteStatsByDay();

    void backupCpuUsageStatsByDay();



}
