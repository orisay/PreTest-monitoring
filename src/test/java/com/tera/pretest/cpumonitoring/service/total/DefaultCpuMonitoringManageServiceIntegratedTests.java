package com.tera.pretest.cpumonitoring.service.total;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.manager.MinuteStatDataBufferManager;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.util.ProviderDateUtil;
import com.tera.pretest.cpumonitoring.core.helper.DaySetupHelper;
import com.tera.pretest.cpumonitoring.core.helper.HourSetupHelper;
import com.tera.pretest.cpumonitoring.core.helper.MinuteSetupHelper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.hardware.CentralProcessor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Log4j2
@SpringBootTest
@DisplayName("DefaultCpuMonitoringManageServiceTotalTest")
public class DefaultCpuMonitoringManageServiceIntegratedTests {

    private final CentralProcessor centralProcessor;

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    private final CpuMonitoringBackupService cpuMonitoringBackupService;

    private final MinuteStatDataBufferManager minuteStatDataBufferManager;

    private final BuildFactory buildFactory;

    private final FormatterConfig formatterConfig;

    private final ProviderDateUtil dateUtil;

    private final HourSetupHelper hourSetupHelper;
    private final MinuteSetupHelper minuteSetupHelper;
    private final DaySetupHelper daySetupHelper;

    @PersistenceContext
    private EntityManager entityManager;

    private DateTimeFormatter dateTimeFormatter;

    @Autowired
    public DefaultCpuMonitoringManageServiceIntegratedTests(CentralProcessor centralProcessor
            , CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository
            , CpuUsageRateByHourRepository cpuUsageRateByHourRepository
            , CpuUsageRateByDayRepository cpuUsageRateByDayRepository
            , CpuMonitoringBackupService cpuMonitoringBackupService
            , MinuteStatDataBufferManager minuteStatDataBufferManager
            , BuildFactory buildFactory, FormatterConfig formatterConfig
            , ProviderDateUtil dateUtil
            , MinuteSetupHelper minuteSetupHelper, HourSetupHelper hourSetupHelper
            , DaySetupHelper daySetupHelper
    ) {
        this.centralProcessor = centralProcessor;
        this.cpuUsageRateByMinuteRepository = cpuUsageRateByMinuteRepository;
        this.cpuUsageRateByHourRepository = cpuUsageRateByHourRepository;
        this.cpuUsageRateByDayRepository = cpuUsageRateByDayRepository;
        this.cpuMonitoringBackupService = cpuMonitoringBackupService;
        this.minuteStatDataBufferManager = minuteStatDataBufferManager;
        this.buildFactory = buildFactory;
        this.formatterConfig = formatterConfig;
        this.dateUtil = dateUtil;
        this.minuteSetupHelper = minuteSetupHelper;
        this.hourSetupHelper = hourSetupHelper;
        this.daySetupHelper = daySetupHelper;
    }

    @BeforeEach
    public void setupCommon() {
        log.debug("Calling setupCommon");
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }


    @AfterEach
    public void shutUpCommon() {
        log.debug("Calling shutUpCommon");
        entityManager.clear();
    }

    //TODO 저장 - 데이터 조회 성공(조회 값 존재,주입 값 일치) and 실패(예외 처리) 데이터 저장 성공 총 4개 // 분 당 데이터 저장 제일 나중에

    @DisplayName("분 당 데이터 저장")
    @Nested
    class saveMonitoringCpuUsageTests {
        @BeforeEach
        public void setup() {
            log.debug("Calling setup");
        }

        @AfterEach
        public void shutUp() {
            log.debug("Calling shutUp");
        }

    }

    @DisplayName("시 당 데이터 저장")
    @Nested
    class saveAverageCpuUsageByHourTests {
        private ZonedDateTime endDay;
        private ZonedDateTime startDay;
        private List<CpuUsageRateByMinute> cpuAverageStats;

        @BeforeEach
        public void setup() {
            log.debug("Calling setup");
            cpuAverageStats = minuteSetupHelper.getCpuAverageStats();

        }

        public void setupDate() {
            startDay = hourSetupHelper.getStartDay();
            endDay = hourSetupHelper.getEndDay();
            cpuAverageStats = minuteSetupHelper.getCpuAverageStats();
            CpuUsageRateByHour cpuUsageRateByHour = hourSetupHelper.setInsertStat(cpuAverageStats);
            hourSetupHelper.saveOneHourCpuUsageStatsToDb(cpuUsageRateByHour);
        }


        @AfterEach
        public void shutUp() {
            log.debug("Calling shutUp");
        }
    }

    @DisplayName("일 당 데이터 저장")
    @Nested
    class saveAverageCpuUsageByDayTests {
        @BeforeEach
        public void setup() {
            log.debug("Calling setup");
        }

        @AfterEach
        public void shutUp() {
            log.debug("Calling shutUp");
        }
    }

    //TODO 소프트 딜리트 업데이트 성공(빈 값 검사 ,값 주입 검사) 예외처리 / 소프트 딜리트된 데이터 조회 (성공, 빈값) 앞에서 값 주입 검사했으니 pass 예외처리 총 6개

    @DisplayName("분 당 데이터 소프트 딜리트")
    @Nested
    class softDeleteAndBackupCpuUsageStatsByMinuteTests {
        @BeforeEach
        public void setup() {
            log.debug("Calling setup");
        }

        @AfterEach
        public void shutUp() {
            log.debug("Calling shutUp");
        }
    }

    @DisplayName("시 당 데이터 소프트 딜리트")
    @Nested
    class softDeleteAndBackupOutdatedCpuUsageStatsByHourTests {
        @BeforeEach
        public void setup() {
            log.debug("Calling setup");
        }

        @AfterEach
        public void shutUp() {
            log.debug("Calling shutUp");
        }
    }

    @DisplayName("일 당 데이터 소프트 딜리트")
    @Nested
    class softDeleteAndBackupOutdatedCpuUsageStatsByDay {
        @BeforeEach
        public void setup() {
            log.debug("Calling setup");
        }

        @AfterEach
        public void shutUp() {
            log.debug("Calling shutUp");
        }
    }
}
