package com.tera.pretest.core.monitoring;

import com.tera.pretest.core.monitoring.factory.CpuMonitoringFactory;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.monitoring.service.CpuMonitoringManageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.*;
import static com.tera.pretest.core.monitoring.contant.MonitoringScheduledConstant.*;

@Log4j2
@Component
public class CpuMonitoring {
    private final CpuMonitoringManageService cpuMonitoringManageService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final CpuMonitoringBackupService cpuMonitoringBackupService;

    public CpuMonitoring(CpuMonitoringManageService cpuMonitoringManageService,
                         CpuMonitoringBackupService cpuMonitoringBackupService) {
        this.cpuMonitoringManageService = cpuMonitoringManageService;
        this.cpuMonitoringBackupService = cpuMonitoringBackupService;
        int coreCount = Runtime.getRuntime().availableProcessors();
        int threadPoolSize = coreCount * MULTIPLICATION_FOR_MIXED_WORK;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(threadPoolSize, new CpuMonitoringFactory());
        scheduledExecutorService.scheduleAtFixedRate(this::scheduleMonitoringCpuUsage, MEASUREMENT_START_TIME,
                CPU_USAGE_BETWEEN_TIME, TimeUnit.MINUTES);
    }

    private void scheduleMonitoringCpuUsage() {
        cpuMonitoringManageService.saveMonitoringCpuUsage();
    }

    //아래는 Thread 부하 방지를 위해서 시간을 다르게 설정 또는 주어진 기획에 따른 주기 설정

    @Scheduled(cron = EVERY_HOUR_AT_THREE_MINUTE, zone = SERVER_TIME_ZONE)
    public void saveAverageCpuUsageByHour() {
        cpuMonitoringManageService.saveAverageCpuUsageByHour();
    }

    @Scheduled(cron = EVERY_DAY_AT_SIX_MINUTE_OF_MIDNIGHT, zone = SERVER_TIME_ZONE)
    public void saveAverageCpuUsageByDay() {
        cpuMonitoringManageService.saveAverageCpuUsageByDay();
    }

    @Scheduled(cron = EVERY_DAY_AT_NINE_MINUTE_OF_MIDNIGHT, zone = SERVER_TIME_ZONE)
    public void softDeleteAndBackupOutdatedCpuUsageStatsByMinute() {
        cpuMonitoringManageService.softDeleteAndBackupCpuUsageStatsByMinute();
    }

    @Scheduled(cron = EVERY_DAY_AT_TWELVE_MINUTE_OF_MIDNIGHT, zone = SERVER_TIME_ZONE)
    public void softDeleteAndBackupOutdatedCpuUsageStatsByHour() {
        cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByHour();
    }

    @Scheduled(cron = EVERY_DAY_AT_FIFTEEN_MINUTE_OF_MIDNIGHT, zone = SERVER_TIME_ZONE)
    public void softDeleteAndBackupOutdatedCpuUsageStatsByDay(){
        cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByDay();
    }

    @Scheduled(cron = EVERY_MONTH_FIRST_SUNDAY_AT_1AM, zone = SERVER_TIME_ZONE)
    public void hardDeleteOutdatedCpuUsageStatsByMinute(){
        cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByMinute();
    }

    @Scheduled(cron = EVERY_MONTH_FIRST_SUNDAY_AT_2AM, zone = SERVER_TIME_ZONE)
    public void hardDeleteOutdatedCpuUsageStatsByHour(){
        cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByHour();
    }

    @Scheduled(cron = EVERY_MONTH_FIRST_SUNDAY_AT_3AM, zone = SERVER_TIME_ZONE)
    public void hardDeleteOutdatedCpuUsageStatsByDay(){
        cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByDay();
    }


}
