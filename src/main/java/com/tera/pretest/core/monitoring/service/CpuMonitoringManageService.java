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
import com.tera.pretest.core.util.DateUtil;
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

import java.text.DecimalFormat;
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
@Service
public class CpuMonitoringManageService {

    private final CentralProcessor centralProcessor;

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    private final CpuMonitoringBackupService cpuMonitoringBackupService;

    private final MinuteStatDataBufferManager minuteStatDataBufferManager;

    private final BuildFactory buildFactory;

    private final FormatterConfig formatterConfig;

    private DateUtil dateUtil;

    public void saveMonitoringCpuUsage() {
        Double averageCpuUsage = getAverageCpuUsageByOneMinute();
        CpuUsageRateByMinute insertData = buildFactory.getInstance().toBuildByCpuUsageRateByMinute(averageCpuUsage);
        minuteStatDataBufferManager.collectCpuUsageRateByMinuteData(insertData);
    }

    private Double getAverageCpuUsageByOneMinute() {
        return DoubleStream.generate(this::getServerTotalCpuUsageByTenSecond)
                .limit(ONE_MINUTE_COUNT_BY_SEC).average().orElse(0.00);
    }

    private Double getServerTotalCpuUsageByTenSecond() {
        long[] startTicks = centralProcessor.getSystemCpuLoadTicks();
        try {
            TimeUnit.SECONDS.sleep(TEN_SECOND);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.error(" InterruptedException 발생",  exception);
        }
        Double averageCpuUsage = centralProcessor.getSystemCpuLoadBetweenTicks(startTicks) * PERCENTAGE;
        return formatterConfig.changeDecimalFormatCpuUsage(averageCpuUsage);
    }



    @Async("daemonThreadForAsync")
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> saveAverageCpuUsageByHour() {
        List<CpuUsageRateByMinute> cpuAverageStats = getMonitoringCpUsageByOneMinuteStats();
        DoubleSummaryStatistics stats = cpuAverageStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());
        cpuUsageRateByHourRepository.save(buildFactory.getInstance().toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage));
        return AsyncResult.forValue(null);
    }

    private List<CpuUsageRateByMinute> getMonitoringCpUsageByOneMinuteStats() {
        ZonedDateTime endDay = dateUtil.getTodayTruncatedToDay();
        ZonedDateTime startDay = dateUtil.daysAgo(ONE_DAY);
        List<CpuUsageRateByMinute> cpuUsageAverageStats =
                cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay);
        if (cpuUsageAverageStats.isEmpty()){
            throw new ProcessCustomException(NOT_FOUND_DATA);
        }
        return cpuUsageAverageStats;
    }

    @Async("daemonThreadForAsync")
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> saveAverageCpuUsageByDay() {
        List<CpuUsageRateByHour> stats = getMonitoringCpUsageByOneHourStats();
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getAverage)
                .summaryStatistics().getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage)
                .min().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage)
                .max().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
        log.info("BaseCode averageUsage:{},  minimumUsage:{}, maximumUsage:{}",averageUsage,minimumUsage, maximumUsage );
        cpuUsageRateByDayRepository.save(buildFactory.getInstance().toBuildByCpuUsageRateByDay(averageUsage, minimumUsage, maximumUsage));
        return AsyncResult.forValue(null);
    }

    private List<CpuUsageRateByHour> getMonitoringCpUsageByOneHourStats() {
        ZonedDateTime endDay = dateUtil.getTodayTruncatedToDay();
        ZonedDateTime startDay = dateUtil.getSearchDay(ONE_DAY);
        List<CpuUsageRateByHour> cpuUsageStats = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
        if (cpuUsageStats.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        return cpuUsageStats;
    }

    //아래부터는 softDelete 작업
    @Async
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> softDeleteAndBackupCpuUsageStatsByMinute() {
        ZonedDateTime pastDay = dateUtil.getSearchDay(ONE_WEEK);
        cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay);
        List<CpuUsageRateByMinute> oldData = cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG);
        if(oldData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);
        return AsyncResult.forValue(null);
    }

    @Async
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> softDeleteAndBackupOutdatedCpuUsageStatsByHour() {
        ZonedDateTime pastDay = dateUtil.getSearchMonth(THREE_MONTH);
        cpuUsageRateByHourRepository.softDeleteOldData(pastDay);
        List<CpuUsageRateByHour> oldData = cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG);
        if(oldData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);
        return AsyncResult.forValue(null);
    }

    @Async
    @Retryable(value = {ProcessCustomException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> softDeleteAndBackupOutdatedCpuUsageStatsByDay() {
        ZonedDateTime pastDay = dateUtil.getSearchYear(ONE_YEAR);
        cpuUsageRateByDayRepository.softDeleteOldData(pastDay);
        List<CpuUsageRateByDay> oldData =cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG);
        if(oldData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);
        return AsyncResult.forValue(null);
    }

}
