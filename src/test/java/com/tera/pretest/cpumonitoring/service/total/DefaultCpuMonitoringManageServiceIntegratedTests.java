package com.tera.pretest.cpumonitoring.service.total;

import com.tera.pretest.config.IntegratedTestConfig;
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
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.monitoring.service.interfaces.CpuMonitoringManageService;
import com.tera.pretest.core.util.ProviderDateUtil;
import com.tera.pretest.core.util.TimeProvider;
import com.tera.pretest.cpumonitoring.core.helper.DaySetupHelper;
import com.tera.pretest.cpumonitoring.core.helper.HourSetupHelper;
import com.tera.pretest.cpumonitoring.core.helper.MinuteSetupHelper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import oshi.hardware.CentralProcessor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@SpringBootTest
@ContextConfiguration(classes = IntegratedTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@ActiveProfiles("totalTest")
@DisplayName("ManageServiceTests")
public class DefaultCpuMonitoringManageServiceIntegratedTests {

    private final CentralProcessor centralProcessor;

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    private final CpuMonitoringManageService cpuMonitoringManageService;

    private final CpuMonitoringBackupService cpuMonitoringBackupService;

    private final MinuteStatDataBufferManager minuteStatDataBufferManager;

    private final BuildFactory buildFactory;

    private final FormatterConfig formatterConfig;

    private final ProviderDateUtil dateUtil;

    private final HourSetupHelper hourSetupHelper;
    private final MinuteSetupHelper minuteSetupHelper;
    private final DaySetupHelper daySetupHelper;

    private final String NOT_MATCH_EXCEPTION = "예외가 일치하지 않습니다.";
    private final String NOT_MATCH_EXCEPTION_MESSAGE = "예외 메세지가 일치하지 않습니다.";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    @Qualifier("basicClock")
    private Clock baseClock;

    @Autowired
    @Qualifier("FixedTestClock")
    private Clock testClock;

    private DateTimeFormatter dateTimeFormatter;

    @Autowired
    public DefaultCpuMonitoringManageServiceIntegratedTests(CentralProcessor centralProcessor
            , CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository
            , CpuUsageRateByHourRepository cpuUsageRateByHourRepository
            , CpuUsageRateByDayRepository cpuUsageRateByDayRepository
            , CpuMonitoringManageService cpuMonitoringManageService
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
        this.cpuMonitoringManageService = cpuMonitoringManageService;
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
        log.info("Calling setupCommon");
        timeProvider.setClockFixedTime(testClock);
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }


    @AfterEach
    public void shutUpCommon() {
        log.info("Calling shutUpCommon");
        timeProvider.setClockFixedTime(baseClock);
        entityManager.close();
    }

    //TODO 저장 - 데이터 조회 성공(주입 값 일치) and 실패(예외 처리) 데이터 저장 성공 총 4개 // 분 당 데이터 저장 제일 나중에

    @DisplayName("분 단위 데이터 저장")
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

    @DisplayName("시 단위 데이터 저장")
    @Nested
    @Transactional
    class saveAverageCpuUsageByHourTests {
        private ZonedDateTime endDay;
        private ZonedDateTime startDay;
        private CpuUsageRateByHour checkCpuUsageRateByHour;
        private List<CpuUsageRateByMinute> checkCpuAverageStats;
        private List<CpuUsageRateByMinute> cpuUsageAverageStats;

        @BeforeEach
        public void setup() {
            minuteSetupHelper.setup();
            minuteSetupHelper.setNotNullFlag();
            minuteSetupHelper.saveDbData();
            hourSetupHelper.setup();
            hourSetupHelper.requestTempData();
            setupDate();
            setUpDbData();
            entityManager.flush();
        }

        public void setupDate() {
            startDay = hourSetupHelper.getStartDay();
            endDay = hourSetupHelper.getEndDay();
        }

        public void setUpDbData() {
            cpuUsageAverageStats = cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay);

            checkCpuUsageRateByHour = hourSetupHelper.getFirstDbData();
            checkCpuAverageStats = minuteSetupHelper.getCpuAverageStats(); //스트림 Arrays.asList(firstDbData,secondDbData);
            CpuUsageRateByHour cpuUsageRateByHour = hourSetupHelper.setInsertStat(checkCpuAverageStats);
            hourSetupHelper.saveOneHourCpuUsageStatsToDb(cpuUsageRateByHour);
        }

        @AfterEach
        public void shutUp() {
        }

        @Test
        @DisplayName("분 단위 데이터 조회")
        @Transactional(readOnly = true)
        public void findForSaveDbData() {
            assertThat(checkCpuAverageStats).isNotEmpty().usingRecursiveComparison()
                    .ignoringFields("createTime", "updateTime", "timeZoneAt", "flag").isEqualTo(cpuUsageAverageStats);
        }

        @Test
        @DisplayName("분 단위 데이터 조회 - 빈 값 ")
        @Transactional
        public void findForSaveDbDataIsEmpty() {
            cpuUsageRateByMinuteRepository.deleteAll();
            assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByHour();
            });
        }

        @Test
        @DisplayName("스트림 결과 체크")
        public void doubleSummaryStatisticsResult() {
            DoubleSummaryStatistics stats = checkCpuAverageStats.stream()
                    .mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
            double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
            double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
            double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());
            CpuUsageRateByHour cpuUsageStat =
                    buildFactory.toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);

            assertThat(cpuUsageStat).isNotNull().usingRecursiveComparison()
                    .ignoringFields("cpuRateByHourSeq", "createTime", "timeZoneAt", "updateTime").isEqualTo(checkCpuUsageRateByHour);

        }

        @Test
        @DisplayName("시 단위 데이터 저장 및 조회 검증 - 동기 처리")
        public void saveCpuUsageStats() {
            cpuUsageRateByHourRepository.deleteAll();

            CpuUsageRateByHour cpuUsageStat = checkCpuUsageRateByHour;
            log.info("saveCpuUsageStats cpuUsageStatValue:{}", cpuUsageStat);
            CpuUsageRateByHour resultData = cpuUsageRateByHourRepository.save(checkCpuUsageRateByHour);
            CpuUsageRateByHour searchData =
                    cpuUsageRateByHourRepository.findByCpuRateByHourSeq(resultData.getCpuRateByHourSeq());
            log.info("saveCpuUsageStats resultData:{}", resultData);
            log.info("saveCpuUsageStats searchData:{}", searchData);

            assertThat(resultData).isNotNull().usingRecursiveComparison()
                    .ignoringFields("createTime", "timeZoneAt", "updateTime").isEqualTo(searchData);

        }

    }


    @DisplayName("일 단위 데이터 저장")
    @Nested
    @Transactional
    class saveAverageCpuUsageByDayTests {
        private ZonedDateTime endDay;
        private ZonedDateTime startDay;
        private CpuUsageRateByDay streamCheck;
        private List<CpuUsageRateByHour> checkCpuAverageStats;
        private List<CpuUsageRateByHour> cpuUsageAverageStats;


        @BeforeEach
        public void setup() {
            log.info("Calling setup");
            hourSetupHelper.setup();
            hourSetupHelper.setNotNullFlag();
            log.info("setup before hourSetupHelper.saveDbData()");
            hourSetupHelper.saveDbData();
            log.info("setup after hourSetupHelper.saveDbData()");
            daySetupHelper.setup();
            daySetupHelper.requestTempData();
            setupDate();
            setUpDbData();
            entityManager.flush();
            //


        }
        public void setupDate() {
            startDay = daySetupHelper.getStartDay();
            endDay = daySetupHelper.getEndDay();
            log.info("setupDate startDay:{}",startDay);
            log.info("setupDate endDay:{}",endDay);
        }

        public void setUpDbData() {
            cpuUsageAverageStats = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
            log.info("setUpDbData cpuUsageAverageStatsValue:{} ",cpuUsageAverageStats);
            checkCpuAverageStats = hourSetupHelper.getCpuAverageStats(); //stream Arrays.asList()

            streamCheck = daySetupHelper.setInsertStat(checkCpuAverageStats);
            daySetupHelper.saveOneDayCpuUsageStatsToDb(streamCheck);
        }

        @AfterEach
        public void shutUp() {
            log.info("Calling shutUp");
        }
        @Test
        @DisplayName("시 단위 데이터 조회")
        @Transactional(readOnly = true)
        public void findForSaveDbData() {
            log.info("checkCpuAverageStatsValue:{}",checkCpuAverageStats);
            log.info("cpuUsageAverageStatsValue:{}",cpuUsageAverageStats);

            assertThat(checkCpuAverageStats).isNotEmpty().usingRecursiveComparison()
                    .ignoringFields("createTime", "updateTime", "timeZoneAt", "flag").isEqualTo(cpuUsageAverageStats);
        }
        @Test
        @DisplayName("시 단위 데이터 조회 - 빈 값 ")
        @Transactional
        public void findForSaveDbDataIsEmpty() {
            cpuUsageRateByHourRepository.deleteAll();
            assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByDay();
            });

        }
        @Test
        @DisplayName("스트림 결과 체크")
        public void doubleSummaryStatisticsAverageResult() {
            double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(checkCpuAverageStats.stream().mapToDouble(CpuUsageRateByHour::getAverage)
                    .summaryStatistics().getAverage());
            double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(checkCpuAverageStats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage)
                    .min().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
            double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(checkCpuAverageStats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage)
                    .max().orElseThrow(() -> new ProcessCustomException(NOT_FOUND_DATA)));
            CpuUsageRateByDay cpuUsageStat = buildFactory.toBuildByCpuUsageRateByDay(averageUsage, minimumUsage, maximumUsage);

            assertThat(cpuUsageStat).isNotNull().usingRecursiveComparison()
                    .ignoringFields("cpuRateByDaySeq", "createTime", "timeZoneAt", "updateTime", "flag")
                    .isEqualTo(streamCheck);
        }


        //TODO  작업중
        @Test
        @DisplayName("시 단위 데이터 저장 및 조회 검증 - 동기 처리")
        public void saveCpuUsageStats() {
//            log.info("saveCpuUsageStats cpuUsageRateByDay value:{}",cpuUsageRateByDay);
//            log.info("saveCpuUsageStats checkDataInHourRepo value:{}",checkDataInHourRepo);
//            log.info("saveCpuUsageStats checkCpuAverageStats value:{}", checkCpuAverageStats);
//            log.info("saveCpuUsageStats  cpuUsageAverageStats value:{}", cpuUsageAverageStats);
//            log.info("saveCpuUsageStats streamCheck value:{}",streamCheck);

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
