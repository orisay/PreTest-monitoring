package com.tera.pretest.core.monitoring.service;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import oshi.hardware.CentralProcessor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.DoubleStream;

import static com.tera.pretest.core.exception.CustomExceptionCode.NOT_FOUND_DATA;
import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.*;

@Log4j2
@AllArgsConstructor
@Service
public class CpuMonitoringManageService {
    private final CentralProcessor centralProcessor;
    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;
    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;
    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;


    @Async("daemonThreadForAsync")
    @Retryable(value = {InterruptedException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    public void saveMonitoringCpuUsage() {
        Double averageCpuUsage = getAverageCpuUsageByOneMinute();
        Double decimalFormatCpuUsage = changeDecimalFormatCpuUsage(averageCpuUsage);
        cpuUsageRateByMinuteRepository.save(CpuUsageRateByMinute.toBuild(decimalFormatCpuUsage));
    }

    private Double getAverageCpuUsageByOneMinute() {
        return DoubleStream.generate(this::getServerTotalCpuUsageByTenSecond)
                .limit(ONE_MINUTE_COUNT_BY_SEC).average().orElse(0.00);
    }

    private Double getServerTotalCpuUsageByTenSecond() {
        long[] startTicks = centralProcessor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(TEN_SECOND_BY_MS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            //TODO log aop update and only log?
            log.error("InterruptedException temp message");
        }
        Double averageCpuUsage = centralProcessor.getSystemCpuLoadBetweenTicks(startTicks) * PERCENTAGE;
        return changeDecimalFormatCpuUsage(averageCpuUsage);
    }

    private Double changeDecimalFormatCpuUsage(Double cpuUsage) {
        DecimalFormat roundUsage = new DecimalFormat("0.00");
        return Double.parseDouble(roundUsage.format(cpuUsage));
    }

    @Async("daemonThreadForAsync")
    @Retryable(value = {CustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    public void saveAverageCpuUsageByHour() {
        List<CpuUsageRateByMinute> cpuAverageStats = getMonitoringCpUsageByOneMinuteStats();
        DoubleSummaryStatistics stats = cpuAverageStats.stream().mapToDouble(CpuUsageRateByMinute::getUsage).summaryStatistics();
        double averageUsage = changeDecimalFormatCpuUsage(stats.getAverage());
        double minimumUsage = changeDecimalFormatCpuUsage(stats.getMin());
        double maximumUsage = changeDecimalFormatCpuUsage(stats.getMax());
        cpuUsageRateByHourRepository.save(CpuUsageRateByHour.toBuild(averageUsage, minimumUsage, maximumUsage));
    }

    private List<CpuUsageRateByMinute> getMonitoringCpUsageByOneMinuteStats() {
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime startTime = endTime.minusHours(ONE_HOUR);
        List<CpuUsageRateByMinute> cpuUsageAverageStats = cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime);
        if (cpuUsageAverageStats.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return cpuUsageAverageStats;
    }

    @Async("daemonThreadForAsync")
    @Retryable(value = {CustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    public void saveAverageCpuUsageByDay() {
        List<CpuUsageRateByHour> stats = getMonitoringCpUsageByOneHourStats();
        double averageUsage = changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getAverage)
                .summaryStatistics().getAverage());
        double minimumUsage = changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage)
                .min().orElseThrow(() -> new CustomException(NOT_FOUND_DATA)));
        double maximumUsage = changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage)
                .max().orElseThrow(() -> new CustomException(NOT_FOUND_DATA)));
        cpuUsageRateByDayRepository.save(CpuUsageRateByDay.toBuild(averageUsage, minimumUsage, maximumUsage));

    }

    private List<CpuUsageRateByHour> getMonitoringCpUsageByOneHourStats() {
        LocalDateTime endDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startDay = endDay.minusDays(ONE_DAY);
        List<CpuUsageRateByHour> cpuUsageStats = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
        if (cpuUsageStats.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return cpuUsageStats;
    }
}
