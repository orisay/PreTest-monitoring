package com.tera.pretest.core.monitoring.service;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.exception.CustomException;
import com.tera.pretest.core.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oshi.hardware.CentralProcessor;

import java.sql.Timestamp;
import java.text.DecimalFormat;
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
    private final CpuMonitoringBackupService cpuMonitoringBackupService;
    private DateUtil dateUtil;


    @Async("daemonThreadForAsync")
    @Retryable(value = {InterruptedException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAverageCpuUsageByHour() {
        List<CpuUsageRateByMinute> cpuAverageStats = getMonitoringCpUsageByOneMinuteStats();
        DoubleSummaryStatistics stats = cpuAverageStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
        double averageUsage = changeDecimalFormatCpuUsage(stats.getAverage());
        double minimumUsage = changeDecimalFormatCpuUsage(stats.getMin());
        double maximumUsage = changeDecimalFormatCpuUsage(stats.getMax());
        cpuUsageRateByHourRepository.save(CpuUsageRateByHour.toBuild(averageUsage, minimumUsage, maximumUsage));
    }

    private List<CpuUsageRateByMinute> getMonitoringCpUsageByOneMinuteStats() {
        Timestamp endTime = dateUtil.getTodayTruncatedToHour();
        Timestamp startTime = dateUtil.getSearchHour(ONE_HOUR);
        List<CpuUsageRateByMinute> cpuUsageAverageStats =
                cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime);
        if (cpuUsageAverageStats.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return cpuUsageAverageStats;
    }

    @Async("daemonThreadForAsync")
    @Retryable(value = {CustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        Timestamp endDay = dateUtil.getTodayTruncatedToDay();
        Timestamp startDay = dateUtil.getSearchDay(ONE_DAY);
        List<CpuUsageRateByHour> cpuUsageStats = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
        if (cpuUsageStats.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return cpuUsageStats;
    }

    //아래부터는 softDelete 작업
    @Async
    @Retryable(value = {CustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDeleteAndBackupCpuUsageStatsByMinute() {
        Timestamp pastDay = dateUtil.getSearchDay(ONE_WEEK);
        cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay);
        List<CpuUsageRateByMinute> oldData = cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG);
        cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);
    }

    @Async
    @Retryable(value = {CustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDeleteAndBackupOutdatedCpuUsageStatsByHour() {
        Timestamp pastDay = dateUtil.getSearchMonth(THREE_MONTH);
        cpuUsageRateByHourRepository.softDeleteOldData(pastDay);
        List<CpuUsageRateByHour> oldData = cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG);
        cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);
    }

    @Async
    @Retryable(value = {CustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDeleteAndBackupOutdatedCpuUsageStatsByDay() {
        Timestamp pastDay = dateUtil.getSearchYear(ONE_YEAR);
        cpuUsageRateByDayRepository.softDeleteOldData(pastDay);
        List<CpuUsageRateByDay> oldData =cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG);
        cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);
    }

}
