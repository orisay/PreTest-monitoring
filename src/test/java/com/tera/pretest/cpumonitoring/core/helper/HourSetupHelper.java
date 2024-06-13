package com.tera.pretest.cpumonitoring.core.helper;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.core.config.FormatterConfig;
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

import static com.tera.pretest.core.contant.MonitoringConstant.DELETE_FLAG;

@Log4j2
@Transactional
@Component
public class HourSetupHelper implements SetupForTestProcess {

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final FormatterConfig formatterConfig;

    private final BuildFactory buildFactory;

    public HourSetupHelper(CpuUsageRateByHourRepository cpuUsageRateByHourRepository, FormatterConfig formatterConfig, BuildFactory buildFactory) {
        this.cpuUsageRateByHourRepository = cpuUsageRateByHourRepository;
        this.formatterConfig = formatterConfig;
        this.buildFactory = buildFactory;
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


    @Override
    public void setup() {
        dataCreator();
    }

    public void requestTempData() {
        log.debug("Calling requestTempData ");
        String startDayString = "2024-05-23T00:00:00.000+09:00";
        String endDayString = "2024-05-24T00:00:00.000+09:00";
        startDay = ZonedDateTime.parse(startDayString, dateTimeFormatter);
        endDay = ZonedDateTime.parse(endDayString, dateTimeFormatter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dataCreator() {
        log.debug("Calling dataCreator ");
        String insertFirstTempStringDate = "2024-05-23T00:00:00.000+09:00";
        String insertSecondTempStringDate = "2024-05-23T12:00:00.000+09:00";
        firstDbData = saveTempDbData(insertFirstTempStringDate, 1L, 50.00, 25.00, 75.00);
        secondDbData = saveTempDbData(insertSecondTempStringDate, 2L, 30.0, 0.00, 60.00);
        cpuAverageStats = Arrays.asList(firstDbData,secondDbData);
    }

    private CpuUsageRateByHour saveTempDbData(String date, long seq, double average, double min, double max) {
        log.debug("Calling saveTempDbData dateValue :{}, seq:{}, average:{}, min:{}, max:{}", date, seq, average, min, max);
        ZonedDateTime insertTimeZoneAt = ZonedDateTime.parse(date, dateTimeFormatter);
        log.debug("Calling tempResultData insertTimeZoneAt:{} ", insertTimeZoneAt);

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

    public void saveDbData() {
        prepareTempDbData(firstDbData);
        prepareTempDbData(secondDbData);
    }

    private CpuUsageRateByHour prepareTempDbData(CpuUsageRateByHour tempDbData) {
        CpuUsageRateByHour result = cpuUsageRateByHourRepository.save(tempDbData);
        log.debug("Calling tempResultData save seq:{}", result.getCpuRateByHourSeq());
        log.debug("Calling tempResultData save average:{}", result.getAverage());
        log.debug("Calling tempResultData save min:{}", result.getMinimumUsage());
        log.debug("Calling tempResultData save max:{}", result.getMaximumUsage());
        log.debug("Calling tempResultData save createTime:{}", result.getCreateTime());
        log.debug("Calling tempResultData save updateTime:{}", result.getUpdateTime());
        log.debug("Calling tempResultData save timeZoneAt:{}", result.getTimeZoneAt());
        log.debug("Calling tempResultData save flag:{}", result.getFlag());
        return result;
    }

    public void setFlagForDeletionAndBackup() {
        firstDbData.setFlag(DELETE_FLAG);
        secondDbData.setFlag(DELETE_FLAG);
    }
    public CpuUsageRateByHour setInsertStat(List<CpuUsageRateByMinute> cpuAverageStats){
        DoubleSummaryStatistics stats = cpuAverageStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
        double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
        double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
        double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());
        CpuUsageRateByHour cpuUsageStat = buildFactory.toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);
        return cpuUsageStat;
    }

    public void saveOneHourCpuUsageStatsToDb(CpuUsageRateByHour cpuUsageStat){
        cpuUsageRateByHourRepository.save(cpuUsageStat);
    }
}
