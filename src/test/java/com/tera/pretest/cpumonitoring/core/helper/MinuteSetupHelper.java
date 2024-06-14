package com.tera.pretest.cpumonitoring.core.helper;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.util.TimeProvider;
import com.tera.pretest.core.util.interfaces.DateUtil;
import com.tera.pretest.cpumonitoring.core.helper.interfaces.SetupForTestProcess;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static com.tera.pretest.core.contant.MonitoringConstant.DELETE_FLAG;
import static com.tera.pretest.core.contant.MonitoringConstant.ONE_DAY;

@Log4j2
@Transactional
@Component
public class MinuteSetupHelper implements SetupForTestProcess {

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final FormatterConfig formatterConfig;

    private final BuildFactory buildFactory;

    private final DateUtil dateUtil;

    private final TimeProvider timeProvider;

    public MinuteSetupHelper(CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository
            , FormatterConfig formatterConfig, BuildFactory buildFactory, DateUtil dateUtil
            , TimeProvider timeProvider) {
        this.cpuUsageRateByMinuteRepository = cpuUsageRateByMinuteRepository;
        this.formatterConfig = formatterConfig;
        this.buildFactory = buildFactory;
        this.dateUtil = dateUtil;
        this.timeProvider = timeProvider;
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
        log.debug("Calling requestTempData");
        ZonedDateTime tempTime =timeProvider.getCurrentZonedDateTimeAt();
        startTime = dateUtil.truncateZonedDateTimeToHour(tempTime);
        endTime = dateUtil.addOneHour(startTime);
        log.info("requestTempDataTempTime:{}",tempTime);
        log.info("requestTempDataStartTime:{}",startTime);
        log.info("requestTempDataEndTime:{}",endTime);
    }

    @Override
    public void dataCreator() {
        log.debug("Calling saveTempDbData BeforeEach");
        String insertFirstTempStringDate = "2024-05-24T00:01:00.000+09:00";
        String insertSecondTempStringDate = "2024-05-24T00:02:00.000+09:00";
        firstDbData = saveTempDbData(insertFirstTempStringDate, 1L, 50.00);
        secondDbData = saveTempDbData(insertSecondTempStringDate, 2L, 30.00);
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveDbData() {
        prepareTempDbData(firstDbData);
        prepareTempDbData(secondDbData);
    }
    private CpuUsageRateByMinute prepareTempDbData(CpuUsageRateByMinute tempDbData) {
        CpuUsageRateByMinute result = cpuUsageRateByMinuteRepository.save(tempDbData);

        log.info("Calling prepareTempDbData save seq:{}", result.getCpuRateByMinuteSeq());
        log.info("Calling prepareTempDbData save usage:{}", result.getUsageRate());
        log.info("Calling prepareTempDbData save createTime:{}", result.getCreateTime());
        log.info("Calling prepareTempDbData save flag:{}", result.getFlag());
        log.info("Calling prepareTempDbData save updateTime:{}", result.getUpdateTime());
        log.info("Calling prepareTempDbData save timeZoneAt:{}", result.getTimeZoneAt());
        return result;
    }

    public void setNotNullFlag(){
        firstDbData.setFlag("N");
        secondDbData.setFlag("N");
    }


    public void setFlagForDeletionAndBackup(){
        firstDbData.setFlag(DELETE_FLAG);
        secondDbData.setFlag(DELETE_FLAG);
    }


}
