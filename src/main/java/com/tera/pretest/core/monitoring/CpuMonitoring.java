package com.tera.pretest.core.monitoring;

import com.tera.pretest.core.monitoring.factory.CpuMonitoringFactory;
import com.tera.pretest.core.monitoring.service.CpuMonitoringManageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.*;

@Log4j2
@Component
public class CpuMonitoring {
    private final CpuMonitoringManageService cpuMonitoringManageService;
    private final ScheduledExecutorService scheduledExecutorService;

    public CpuMonitoring(CpuMonitoringManageService cpuMonitoringManageService) {
        this.cpuMonitoringManageService = cpuMonitoringManageService;
        int coreCount = Runtime.getRuntime().availableProcessors();
        int threadPoolSize = coreCount * MULTIPLICATION_FOR_MIXED_WORK;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(threadPoolSize, new CpuMonitoringFactory());
        scheduledExecutorService.scheduleAtFixedRate(this::scheduleMonitoringCpuUsage, MEASUREMENT_START_TIME, CPU_USAGE_BETWEEN_TIME, TimeUnit.MINUTES);
    }

    private void scheduleMonitoringCpuUsage() {
        cpuMonitoringManageService.saveMonitoringCpuUsage();
    }

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void saveAverageCpuUsageByHour() {
        cpuMonitoringManageService.saveAverageCpuUsageByHour();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void saveAverageCpuUsageByDay() {
        cpuMonitoringManageService.saveAverageCpuUsageByDay();
    }


}
