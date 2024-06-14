package com.tera.pretest.cpumonitoring.service.total;

import com.tera.pretest.config.IntegratedTestConfig;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import com.tera.pretest.core.exception.restful.CustomException;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.tera.pretest.core.contant.MonitoringConstant.ONE_DAY;
import static com.tera.pretest.core.exception.restful.CustomExceptionCode.NOT_FOUND_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Log4j2
@SpringBootTest
@ContextConfiguration(classes = IntegratedTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@ActiveProfiles("totalTest")
@DisplayName("MonitoringServiceTest")
public class CpuMonitoringServiceIntegratedTests {

    @Autowired
    public CpuMonitoringServiceIntegratedTests(CpuMonitoringService cpuMonitoringService
            , CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository
            , CpuUsageRateByHourRepository cpuUsageRateByHourRepository
            , CpuUsageRateByDayRepository cpuUsageRateByDayRepository
            , ProviderDateUtil dateUtil, MinuteSetupHelper minuteSetupHelper
            , HourSetupHelper hourSetupHelper, DaySetupHelper daySetupHelper
    ) {
        this.cpuMonitoringService = cpuMonitoringService;
        this.cpuUsageRateByMinuteRepository = cpuUsageRateByMinuteRepository;
        this.cpuUsageRateByHourRepository = cpuUsageRateByHourRepository;
        this.cpuUsageRateByDayRepository = cpuUsageRateByDayRepository;
        this.dateUtil = dateUtil;
        this.minuteSetupHelper = minuteSetupHelper;
        this.hourSetupHelper = hourSetupHelper;
        this.daySetupHelper = daySetupHelper;
    }

    private final CpuMonitoringService cpuMonitoringService;

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    private final ProviderDateUtil dateUtil;

    private final MinuteSetupHelper minuteSetupHelper;

    private final HourSetupHelper hourSetupHelper;

    private final DaySetupHelper daySetupHelper;

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

    private final String NOT_MATCH_EXCEPTION = "예외가 일치하지 않습니다.";
    private final String NOT_MATCH_EXCEPTION_MESSAGE = "예외 메세지가 일치하지 않습니다.";


    @BeforeEach
    public void setupBasic() {
        log.info("Calling setupBasic");
        timeProvider.setClockFixedTime(testClock);
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    @AfterEach
    public void shutUp() {
        log.info("Calling shutUp");
        timeProvider.setClockFixedTime(baseClock);
        entityManager.close();
    }

    @Nested
    @Transactional
    @DisplayName("특정한 1시간 분 단위 데이터 조회")
    class SuccessCaseGetCpuUsageRateByMinute {
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;

        @BeforeEach
        public void setup() {
            log.info("Calling setup BeforeEach");
            minuteSetupHelper.requestTempData();
            minuteSetupHelper.setup();
            setDateData();
            minuteSetupHelper.setNotNullFlag();
            minuteSetupHelper.saveDbData();
            entityManager.flush();
        }

        private void setDateData() {
            log.debug("Calling setDateData");
            startTime = minuteSetupHelper.getStartTime();
            endTime = minuteSetupHelper.getEndTime();
        }

        @Test
        @DisplayName("시간 선후 결과 검증")
        public void conditionRequestDate() {
            assertThat(endTime).isAfter(startTime);
        }

        @Test
        @DisplayName("리스트 값 존재")
        @Transactional(readOnly = true)
        public void testResultNotEmpty() {
            List<CpuUsageRateByMinute> dbResult = cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime);
            log.info("DB result List Value:{}", dbResult);
            assertThat(dbResult).isNotEmpty();
        }

        @Test
        @DisplayName("리스트 값 예상값 검증")
        @Transactional(readOnly = true)
        public void conditionListValue() {
            List<CpuUsageRateByMinute> dbResult = cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime);
            assertThat(dbResult.get(0).getUsageRate()).isEqualTo(50.00);
        }

        @Test
        @DisplayName("리스트 값이 빈 값")
        public void resultValueIsEmpty() {
            cpuUsageRateByMinuteRepository.deleteAll();

            CustomException exception = assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByMinute(new GetCpuUsageRateByMinute(startTime));
            }, NOT_MATCH_EXCEPTION);

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);
        }
    }

    @Transactional
    @Nested
    @DisplayName("특정한 1일 시 단위 데이터 조회")
    class SuccessCaseGetCpuUsageRateByHour {
        private ZonedDateTime startDay;
        private ZonedDateTime endDay;

        @BeforeEach
        public void setup() {
            log.info("Calling setup BeforeEach");
            hourSetupHelper.requestTempData();
            hourSetupHelper.setup();
            hourSetupHelper.setNotNullFlag();
            hourSetupHelper.saveDbData();
            setDateData();
            entityManager.flush();
        }

        private void setDateData() {
            log.debug("Calling setDateData");
            startDay = hourSetupHelper.getStartDay();
            endDay = hourSetupHelper.getEndDay();
        }

        @Test
        @DisplayName("시간 선후 결과 검증")
        public void conditionRequestDate() {
            assertThat(endDay).isAfter(startDay);
        }

        @Test
        @DisplayName("리스트 값 존재")
        @Transactional(readOnly = true)
        public void testResultNotEmpty() {
            List<CpuUsageRateByHour> dbResult = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
            log.debug("DB result List Value:{}", dbResult);
            assertThat(dbResult).isNotEmpty();
        }

        @Test
        @DisplayName("리스트 값 예상값 검증")
        @Transactional(readOnly = true)
        public void conditionListValue() {
            log.debug("conditionListValueStartDay:{}",startDay);
            log.debug("conditionListValueEndDay:{}",endDay);
            List<CpuUsageRateByHour> dbResult = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
            log.debug("Calling conditionListValue dbResult:{}", dbResult);
            assertThat(dbResult.get(0).getAverage()).isEqualTo(40.00);
        }

        @Test
        @DisplayName("리스트 값이 빈 값")
        public void resultValueIsEmpty() {
            cpuUsageRateByHourRepository.deleteAll();

            CustomException exception = assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByHour(new GetCpuUsageRateByHour(startDay));
            }, NOT_MATCH_EXCEPTION);

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);
        }
    }

    @Transactional
    @Nested
    @DisplayName("특정 기간 일 단위 데이터 조회")
    class SuccessCaseGetCpuUsageRateByDay {
        private ZonedDateTime startDay;
        private ZonedDateTime endDay;


        @BeforeEach
        public void setup() {
            log.info("Calling setup BeforeEach");
            daySetupHelper.requestTempData();
            daySetupHelper.setup();
            daySetupHelper.setNotNullFlag();
            daySetupHelper.saveDbData();
            setDateData();
            entityManager.flush();
        }

        private void setDateData() {
            log.debug("Calling setDateData");
            startDay = daySetupHelper.getStartDay();
            endDay = daySetupHelper.getEndDay();
        }

        @Test
        @DisplayName("exactEndDayData 간접 테스트 - true Case")
        public void trueResultExactEndDayData() {
            ZonedDateTime firstTestEndDay = timeProvider.getCurrentZonedDateTimeAt().
                    truncatedTo(ChronoUnit.DAYS);
            boolean isFirstSameDay = dateUtil.isSameDay(firstTestEndDay);
            ZonedDateTime secondTestEndDay = dateUtil.addOneDay(firstTestEndDay);
            boolean isSecondSameDay = dateUtil.isSameDay(secondTestEndDay);
            assertTrue(isFirstSameDay, "Test True Case Result false");
            assertFalse(isSecondSameDay, "Retry Test fail");
        }

        @Test
        @DisplayName("exactEndDayData 간접 테스트 - false Case")
        public void falseResultExactEndDayData() {
            ZonedDateTime firstTestEndDay = timeProvider.getCurrentZonedDateTimeAt()
                    .truncatedTo(ChronoUnit.DAYS).minusDays(ONE_DAY);
            boolean isFirstSameDay = dateUtil.isSameDay(firstTestEndDay);
            assertFalse(isFirstSameDay, "Test False Case Result True");
        }

        @Test
        @DisplayName("시간 선후 결과 검증")
        public void conditionRequestDate() {
            assertThat(endDay).isAfter(startDay);
        }

        @Test
        @DisplayName("리스트 값 존재")
        @Transactional(readOnly = true)
        public void testResultNotEmpty() {
            log.info("conditionListValueStartDay:{}",startDay);
            log.info("conditionListValueEndDay:{}",endDay);
            List<CpuUsageRateByDay> dbResult = cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay);
            log.info("DB result List Value:{}", dbResult);
            assertThat(dbResult).isNotEmpty();
        }

        @Test
        @DisplayName("리스트 값 예상값 검증")
        @Transactional(readOnly = true)
        public void conditionListValue() {
            List<CpuUsageRateByDay> dbResult = cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay);
            log.info("Calling conditionListValue dbResult:{}", dbResult);
            assertThat(dbResult.get(0).getAverage()).isEqualTo(50.00);
        }

        @Test
        @DisplayName("리스트 값이 빈 값")
        public void resultValueIsEmpty() {
            cpuUsageRateByDayRepository.deleteAll();

            CustomException exception = assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByDay(new GetCpuUsageRateByDay(startDay, endDay));
            }, NOT_MATCH_EXCEPTION);

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);
        }

    }
}
