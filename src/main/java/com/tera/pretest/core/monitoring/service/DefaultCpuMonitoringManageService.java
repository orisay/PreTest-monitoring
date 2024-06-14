package com.tera.pretest.core.monitoring.service;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.manager.MinuteStatDataBufferManager;
import com.tera.pretest.core.monitoring.service.interfaces.CpuMonitoringManageService;
import com.tera.pretest.core.util.ProviderDateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oshi.hardware.CentralProcessor;

import java.time.ZonedDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

import static com.tera.pretest.core.contant.MonitoringConstant.*;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;

@Log4j2
@AllArgsConstructor
@Transactional
@Service
public class DefaultCpuMonitoringManageService implements CpuMonitoringManageService {

    private final CentralProcessor centralProcessor;

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    private final CpuMonitoringBackupService cpuMonitoringBackupService;

    private final MinuteStatDataBufferManager minuteStatDataBufferManager;

    private final BuildFactory buildFactory;

    private final FormatterConfig formatterConfig;

    private ProviderDateUtil dateUtil;

    @Override
    public void saveMonitoringCpuUsage() {
        Double averageCpuUsage = getAverageCpuUsageByOneMinute();
        CpuUsageRateByMinute insertData = buildFactory.toBuildByCpuUsageRateByMinute(averageCpuUsage);
        minuteStatDataBufferManager.collectCpuUsageRateByMinuteData(insertData);
    }

    @Override
    public void threadSleep(long second) throws InterruptedException {
        TimeUnit.SECONDS.sleep(second);
    }

    private Double getAverageCpuUsageByOneMinute() {
        return DoubleStream.generate(this::getServerTotalCpuUsageByTenSecond)
                .limit(ONE_MINUTE_COUNT_BY_SEC).average().orElse(0.00);
    }

    private Double getServerTotalCpuUsageByTenSecond() {
        long[] startTicks = centralProcessor.getSystemCpuLoadTicks();
        try {
            threadSleep(TEN_SECOND);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.error(" InterruptedException 발생", exception);
        }
        Double averageCpuUsage = centralProcessor.getSystemCpuLoadBetweenTicks(startTicks) * PERCENTAGE;
        return formatterConfig.changeDecimalFormatCpuUsage(averageCpuUsage);
    }

    @Override
    public void saveAverageCpuUsageByHour() {
        List<CpuUsageRateByMinute> cpuAverageStats = getMonitoringCpUsageByOneMinuteStats();
        DoubleSummaryStatistics stats = cpuAverageStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());
        CpuUsageRateByHour cpuUsageStat = buildFactory.toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);
        saveOneHourCpuUsageStatsToDb(cpuUsageStat);
    }

    private List<CpuUsageRateByMinute> getMonitoringCpUsageByOneMinuteStats() {
        ZonedDateTime endDay = dateUtil.getTodayTruncatedToDay();
        ZonedDateTime startDay = dateUtil.daysAgo(ONE_DAY);
        List<CpuUsageRateByMinute> cpuUsageAverageStats =
                cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay);
        if (cpuUsageAverageStats.isEmpty()) {
            log.warn("getMonitoringCpUsageByOneMinuteStats startDay:{}, endDay:{}",startDay, endDay);
            log.warn("getMonitoringCpUsageByOneMinuteStats cpuUsageAverageStats:{}",cpuUsageAverageStats);
            throw new ProcessCustomException(NOT_FOUND_DATA);
        }
        return cpuUsageAverageStats;
    }

    @Async("daemonThreadForAsync")
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Future<Void> saveOneHourCpuUsageStatsToDb(CpuUsageRateByHour cpuUsageStat) {
        cpuUsageRateByHourRepository.save(cpuUsageStat);
        return AsyncResult.forValue(null);
    }

    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(readOnly = true)
    @Override
    public void saveAverageCpuUsageByDay() {
        List<CpuUsageRateByHour> stats = getMonitoringCpUsageByOneHourStats();
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getAverage)
                .summaryStatistics().getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage)
                .min().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage)
                .max().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
        CpuUsageRateByDay cpuUsageStat = buildFactory.toBuildByCpuUsageRateByDay(averageUsage, minimumUsage, maximumUsage);
        saveOneDayCpuUsageStatsToDb(cpuUsageStat);

    }

    private List<CpuUsageRateByHour> getMonitoringCpUsageByOneHourStats() {
        ZonedDateTime endDay = dateUtil.getTodayTruncatedToDay();
        ZonedDateTime startDay = dateUtil.getSearchDay(ONE_DAY);
        List<CpuUsageRateByHour> cpuUsageStats = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
        if (cpuUsageStats.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        return cpuUsageStats;
    }

    @Async("daemonThreadForAsync")
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Future<Void> saveOneDayCpuUsageStatsToDb(CpuUsageRateByDay cpuUsageStat) {
        cpuUsageRateByDayRepository.save(cpuUsageStat);
        return AsyncResult.forValue(null);
    }

    @Override
    public void softDeleteAndBackupCpuUsageStatsByMinute() {
        softDeleteStatsByMinute();
        backupCpuUsageStatsByMinute();
    }

    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void softDeleteStatsByMinute() {
        ZonedDateTime pastDay = dateUtil.getSearchDay(ONE_WEEK);
        long softDeleteEffectDbRaw = cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay);
        log.debug("DB Side Effect raw:{}", softDeleteEffectDbRaw);
    }

    @Async
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Future<Void> backupCpuUsageStatsByMinute() {
        List<CpuUsageRateByMinute> oldData = cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG);
        if (oldData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);
        return AsyncResult.forValue(null);
    }

    @Override
    public void softDeleteAndBackupOutdatedCpuUsageStatsByHour() {
        softDeleteStatsByHour();
        backupCpuUsageStatsByHour();
    }

    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void softDeleteStatsByHour() {
        ZonedDateTime pastDay = dateUtil.getSearchMonth(THREE_MONTH);
        long softDeleteEffectDbRaw = cpuUsageRateByHourRepository.softDeleteOldData(pastDay);
        log.debug("DB Side Effect raw:{}", softDeleteEffectDbRaw);
    }

    @Async
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Future<Void> backupCpuUsageStatsByHour() {
        List<CpuUsageRateByHour> oldData = cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG);
        if (oldData.isEmpty()) {
            throw new ProcessCustomException(NOT_FOUND_DATA);
        }
        cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);
        return AsyncResult.forValue(null);
    }

    @Override
    public void softDeleteAndBackupOutdatedCpuUsageStatsByDay() {
        softDeleteStatsByDay();
        backupCpuUsageStatsByDay();
    }

    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void softDeleteStatsByDay() {
        ZonedDateTime pastDay = dateUtil.getSearchYear(ONE_YEAR);
        long softDeleteEffectDbRaw = cpuUsageRateByDayRepository.softDeleteOldData(pastDay);
        log.debug("DB Side Effect raw:{}", softDeleteEffectDbRaw);
    }

    @Async
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Future<Void> backupCpuUsageStatsByDay() {
        List<CpuUsageRateByDay> oldData = cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG);
        if (oldData.isEmpty()) {
            throw new ProcessCustomException(NOT_FOUND_DATA);
        }
        cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);
        return AsyncResult.forValue(null);
    }

    @Override
    public Future<Void> saveOneMinuteCpuUsageToDb() {
        throw new UnsupportedOperationException("이 구현체에서 사용되지 않습니다..");
    }

}
