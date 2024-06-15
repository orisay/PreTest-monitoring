package com.tera.pretest.cpumonitoring.core.helper;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.core.config.FormatterConfig;
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
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static com.tera.pretest.core.constant.MonitoringConstant.DELETE_FLAG;

@Log4j2
@Transactional
@Component
public class HourSetupHelper implements SetupForTestProcess {

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final FormatterConfig formatterConfig;

    private final BuildFactory buildFactory;

    private final DateUtil dateUtil;

    private final TimeProvider timeProvider;

    public HourSetupHelper(CpuUsageRateByHourRepository cpuUsageRateByHourRepository, FormatterConfig formatterConfig
            , BuildFactory buildFactory, DateUtil dateUtil, TimeProvider timeProvider) {
        this.cpuUsageRateByHourRepository = cpuUsageRateByHourRepository;
        this.formatterConfig = formatterConfig;
        this.buildFactory = buildFactory;
        this.dateUtil = dateUtil;
        this.timeProvider = timeProvider;
    }

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private ZonedDateTime startDay;
    private ZonedDateTime endDay;
    private CpuUsageRateByHour firstDbData;
    private CpuUsageRateByHour secondDbData;
    private List<CpuUsageRateByHour> cpuAverageStats;




    public ZonedDateTime getStartDay() {
        return startDay;
    }

    public ZonedDateTime getEndDay() {
        return endDay;
    }

    public CpuUsageRateByHour getFirstDbData() {
        return firstDbData;
    }

    public CpuUsageRateByHour getSecondDbData() {
        return secondDbData;
    }

    public List<CpuUsageRateByHour> getCpuAverageStats() {
        return cpuAverageStats;
    }



    @Override
    public void setup() {
        dataCreator();
    }

    public void requestTempData() {
        log.debug("Calling requestTempData");
        ZonedDateTime tempDay =timeProvider.getCurrentZonedDateTimeAt();
        startDay = dateUtil.truncateZonedDateTimeToDay(tempDay);
        endDay = dateUtil.addOneDay(startDay);
        log.info("requestTempDataTempDay:{}",tempDay);
        log.info("requestTempDataStartDay:{}",startDay);
        log.info("requestTempDataEndDay:{}",endDay);
    }

    @Override
    public void dataCreator() {
        log.debug("Calling dataCreator ");
        String insertFirstTempStringDate = "2024-05-24T01:00:00.000+09:00";
        String insertSecondTempStringDate = "2024-05-24T02:00:00.000+09:00";
        firstDbData = saveTempDbData(insertFirstTempStringDate, 1L, 40.00, 30.00, 50.00);
        secondDbData = saveTempDbData(insertSecondTempStringDate, 2L, 30.0, 0.00, 60.00);
        cpuAverageStats = Arrays.asList(firstDbData, secondDbData);
        log.info("dataCreatorcpuAverageStats:{}",cpuAverageStats);
    }

    private CpuUsageRateByHour saveTempDbData(String date, long seq, double average, double min, double max) {
        log.debug("Calling saveTempDbData dateValue :{}, seq:{}, average:{}, min:{}, max:{}", date, seq, average, min, max);
        ZonedDateTime insertTimeZoneAt = ZonedDateTime.parse(date, dateTimeFormatter);
        log.info("Calling tempResultData insertTimeZoneAt:{} ", insertTimeZoneAt);

        CpuUsageRateByHour resultTempDbData = new CpuUsageRateByHour();
        resultTempDbData.setCpuRateByHourSeq(seq);
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
        prepareTempDbData(firstDbData);
        prepareTempDbData(secondDbData);
    }

    private CpuUsageRateByHour prepareTempDbData(CpuUsageRateByHour tempDbData) {
        CpuUsageRateByHour result = cpuUsageRateByHourRepository.save(tempDbData);
        log.info("Calling tempResultData save seq:{}", result.getCpuRateByHourSeq());
        log.info("Calling tempResultData save average:{}", result.getAverage());
        log.info("Calling tempResultData save min:{}", result.getMinimumUsage());
        log.info("Calling tempResultData save max:{}", result.getMaximumUsage());
        log.info("Calling tempResultData save createTime:{}", result.getCreateTime());
        log.info("Calling tempResultData save updateTime:{}", result.getUpdateTime());
        log.info("Calling tempResultData save timeZoneAt:{}", result.getTimeZoneAt());
        log.info("Calling tempResultData save flag:{}", result.getFlag());
        return result;
    }


    public CpuUsageRateByHour setInsertStat(List<CpuUsageRateByMinute> cpuAverageStats) {
        DoubleSummaryStatistics stats = cpuAverageStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());
        CpuUsageRateByHour cpuUsageStat = buildFactory.toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);
        return cpuUsageStat;
    }

    public void saveOneHourCpuUsageStatsToDb(CpuUsageRateByHour cpuUsageStat) {
        cpuUsageRateByHourRepository.save(cpuUsageStat);
    }

    public void setFlagForDeletionAndBackup() {
        firstDbData.setFlag(DELETE_FLAG);
        secondDbData.setFlag(DELETE_FLAG);
    }

    public void setNotNullFlag() {
        firstDbData.setFlag("N");
        secondDbData.setFlag("N");
    }

    //    public CpuUsageRateByHour getCheckDataInHourRepo() {
//        return checkDataInHourRepo;
//    }
}
