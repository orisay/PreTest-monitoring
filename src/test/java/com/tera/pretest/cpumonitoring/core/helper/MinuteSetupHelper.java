package com.tera.pretest.cpumonitoring.core.helper;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.cpumonitoring.core.helper.interfaces.SetupForTestProcess;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
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
@Service
public class MinuteSetupHelper implements SetupForTestProcess {

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final FormatterConfig formatterConfig;

    private final BuildFactory buildFactory;

    public MinuteSetupHelper(CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository, FormatterConfig formatterConfig, BuildFactory buildFactory) {
        this.cpuUsageRateByMinuteRepository = cpuUsageRateByMinuteRepository;
        this.formatterConfig = formatterConfig;
        this.buildFactory = buildFactory;
    }

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private CpuUsageRateByMinute firstDbData;
    private CpuUsageRateByMinute secondDbData;
    private List<CpuUsageRateByMinute> cpuAverageStats;



    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public CpuUsageRateByMinute getFirstDbData() {
        return firstDbData;
    }

    public CpuUsageRateByMinute getSecondDbData() {
        return secondDbData;
    }

    public List<CpuUsageRateByMinute> getCpuAverageStats() {
        return cpuAverageStats;
    }

    @Override
    public void setup() {
        dataCreator();
    }

    public void requestTempData() {
        log.debug("Calling requestTempData BeforeEach");
        String startTimeString = "2024-05-23T01:00:00.000+09:00";
        String endTimeString = "2024-05-23T02:00:00.000+09:00";
        startTime = ZonedDateTime.parse(startTimeString, dateTimeFormatter);
        endTime = ZonedDateTime.parse(endTimeString, dateTimeFormatter);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dataCreator() {
        log.debug("Calling saveTempDbData BeforeEach");
        String insertFirstTempStringDate = "2024-05-23T01:01:00.000+09:00";
        String insertSecondTempStringDate = "2024-05-23T01:02:00.000+09:00";
        firstDbData = saveTempDbData(insertFirstTempStringDate, 1L, 50.00);
        secondDbData = saveTempDbData(insertSecondTempStringDate, 2L, 30.01);
        cpuAverageStats = Arrays.asList(firstDbData,secondDbData);

    }
    private CpuUsageRateByMinute saveTempDbData(String date, long seq, double usage) {
        log.debug("Calling saveTempDbData dateValue :{}, seq:{}, usage:{}", date, seq, usage);
        ZonedDateTime insertTimeZoneAt = ZonedDateTime.parse(date, dateTimeFormatter);
        log.info("Calling tempResultData insertTimeZoneAt:{} ", insertTimeZoneAt);
        CpuUsageRateByMinute resultTempDbData = new CpuUsageRateByMinute();
        resultTempDbData.setCpuRateByMinuteSeq(seq);
        resultTempDbData.setUsageRate(usage);
        resultTempDbData.setTimeZoneAt(insertTimeZoneAt);
        resultTempDbData.setCreateTime(insertTimeZoneAt);
        resultTempDbData.setUpdateTime(insertTimeZoneAt);
        return resultTempDbData;
    }

    public void saveDbData() {
        prepareTempDbData(firstDbData);
        prepareTempDbData(secondDbData);
    }
    private CpuUsageRateByMinute prepareTempDbData(CpuUsageRateByMinute tempDbData) {
        CpuUsageRateByMinute result = cpuUsageRateByMinuteRepository.save(tempDbData);
        log.debug("Calling tempResultData save seq:{}", result.getCpuRateByMinuteSeq());
        log.debug("Calling tempResultData save usage:{}", result.getUsageRate());
        log.debug("Calling tempResultData save createTime:{}", result.getCreateTime());
        log.debug("Calling tempResultData save updateTime:{}", result.getUpdateTime());
        log.debug("Calling tempResultData save timeZoneAt:{}", result.getTimeZoneAt());
        return result;
    }


    public void setFlagForDeletionAndBackup(){
        firstDbData.setFlag(DELETE_FLAG);
        secondDbData.setFlag(DELETE_FLAG);
    }


}
