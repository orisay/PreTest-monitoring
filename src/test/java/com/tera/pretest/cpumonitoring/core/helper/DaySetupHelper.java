package com.tera.pretest.cpumonitoring.core.helper;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.util.TimeProvider;
import com.tera.pretest.core.util.interfaces.DateUtil;
import com.tera.pretest.cpumonitoring.core.helper.interfaces.SetupForTestProcess;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.tera.pretest.core.constant.MonitoringConstant.*;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;

@Log4j2
@Transactional
@Component
public class DaySetupHelper implements SetupForTestProcess {

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;
    private final FormatterConfig formatterConfig;
    private final BuildFactory buildFactory;
    private final DateUtil dateUtil;

    private final TimeProvider timeProvider;

    public DaySetupHelper(CpuUsageRateByDayRepository cpuUsageRateByDayRepository, FormatterConfig formatterConfig
            , BuildFactory buildFactory, DateUtil dateUtil, TimeProvider timeProvider) {
        this.cpuUsageRateByDayRepository = cpuUsageRateByDayRepository;
        this.formatterConfig = formatterConfig;
        this.buildFactory = buildFactory;
        this.dateUtil = dateUtil;
        this.timeProvider = timeProvider;
    }

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private ZonedDateTime startDay;
    private ZonedDateTime endDay;
    private CpuUsageRateByDay firstDbData;
    private CpuUsageRateByDay secondDbData;
    private List<CpuUsageRateByDay> cpuAverageStats;



    public ZonedDateTime getStartDay() {
        return startDay;
    }

    public ZonedDateTime getEndDay() {
        return endDay;
    }

    public CpuUsageRateByDay getFirstDbData() {
        return firstDbData;
    }

    public CpuUsageRateByDay getSecondDbData() {
        return secondDbData;
    }

    public List<CpuUsageRateByDay> getCpuAverageStats() {
        return cpuAverageStats;
    }

    @Override
    public void setup() {
        dataCreator();
    }

    public void requestTempData() {
        log.debug("Calling requestTempData BeforeEach");
        ZonedDateTime tempStartDay =timeProvider.getCurrentZonedDateTimeAt();
        ZonedDateTime tempEndDay =timeProvider.getCurrentZonedDateTimeAt().plusDays(ONE_WEEK);
        startDay = dateUtil.truncateZonedDateTimeToDay(tempStartDay); //2024-05-24T00:00+09:00[Asia/Seoul]
        endDay = dateUtil.truncateZonedDateTimeToDay(tempEndDay); //2024-05-31T00:00+09:00[Asia/Seoul]
    }


    @Override
    public void dataCreator() {
        log.debug("Calling dataCreator");
        String insertFirstTempStringDate = "2024-05-25T00:00:00.000+09:00";
        String insertSecondTempStringDate = "2024-05-26T00:00:00.000+09:00";
        firstDbData = saveTempDbData(insertFirstTempStringDate, 1L, 50.00, 25.00, 75.00);
        secondDbData = saveTempDbData(insertSecondTempStringDate, 2L, 30.0, 0.00, 60.00);
        cpuAverageStats = Arrays.asList(firstDbData,secondDbData);
    }


    private CpuUsageRateByDay saveTempDbData(String date, long seq, double average, double min, double max) {
        log.debug("Calling saveTempDbData dateValue :{}, seq:{}, average:{}, min:{}, max:{}", date, seq, average, min, max);
        ZonedDateTime insertTimeZoneAt = ZonedDateTime.parse(date, dateTimeFormatter);
        log.debug("Calling tempResultData insertTimeZoneAt:{} ", insertTimeZoneAt);

        CpuUsageRateByDay resultTempDbData = new CpuUsageRateByDay();
        resultTempDbData.setCpuRateByDaySeq(seq);
        resultTempDbData.setAverage(average);
        resultTempDbData.setMinimumUsage(min);
        resultTempDbData.setMaximumUsage(max);
        resultTempDbData.setTimeZoneAt(insertTimeZoneAt);
        resultTempDbData.setCreateTime(insertTimeZoneAt);
        resultTempDbData.setUpdateTime(insertTimeZoneAt);
        return resultTempDbData;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveDbData() {
        CpuUsageRateByDay test1 = prepareTempDbData(firstDbData);
        CpuUsageRateByDay test2 = prepareTempDbData(secondDbData);

        log.info("test1:{}",test1);
        log.info("test2:{}",test2);
    }

    private CpuUsageRateByDay prepareTempDbData(CpuUsageRateByDay tempDbData) {
        CpuUsageRateByDay result = cpuUsageRateByDayRepository.save(tempDbData);
        log.info("Calling tempResultData save seq:{}", result.getCpuRateByDaySeq());
        log.info("Calling tempResultData save average:{}", result.getAverage());
        log.info("Calling tempResultData save min:{}", result.getMinimumUsage());
        log.info("Calling tempResultData save max:{}", result.getMaximumUsage());
        log.info("Calling tempResultData save createTime:{}", result.getCreateTime());
        log.info("Calling tempResultData save updateTime:{}", result.getUpdateTime());
        log.info("Calling tempResultData save timeZoneAt:{}", result.getTimeZoneAt());
        log.info("Calling tempResultData save flag:{}", result.getFlag());
        return result;
    }
    public CpuUsageRateByDay setInsertStat(List<CpuUsageRateByHour> stats){
        log.info("DayHelper setInsertStat param:{}",stats);
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getAverage)
                .summaryStatistics().getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage)
                .min().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage)
                .max().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
        CpuUsageRateByDay cpuUsageStat = buildFactory.toBuildByCpuUsageRateByDay(averageUsage, minimumUsage, maximumUsage);
        log.info("DayHelper cpuUsageStat :{}",cpuUsageStat);
        return cpuUsageStat;
    }
    public void saveOneDayCpuUsageStatsToDb(CpuUsageRateByDay cpuUsageStat){
        cpuUsageRateByDayRepository.save(cpuUsageStat);
    }

    public void setFlagForDeletionAndBackup(){
        firstDbData.setFlag(DELETE_FLAG);
        secondDbData.setFlag(DELETE_FLAG);
    }

    public void setNotNullFlag(){
        firstDbData.setFlag("N");
        secondDbData.setFlag("N");
    }
}
