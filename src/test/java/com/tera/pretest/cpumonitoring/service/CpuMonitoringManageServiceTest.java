package com.tera.pretest.cpumonitoring.service;

import com.tera.pretest.config.AsyncTestConfig;
import com.tera.pretest.config.UnitTestConfig;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.exception.process.ProcessCustomExceptionCode;
import com.tera.pretest.core.manager.MinuteStatDataBufferManager;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.monitoring.CpuMonitoring;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.monitoring.service.CpuMonitoringManageService;
import com.tera.pretest.core.util.DateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import oshi.hardware.CentralProcessor;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.tera.pretest.core.contant.MonitoringConstant.*;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Log4j2
@DisplayName("CpuMonitoringManageService Test")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Import({ZonedDateTimeFormatConfig.class, CpuMonitoringManageService.class})
public class CpuMonitoringManageServiceTest {

    @Mock
    private CentralProcessor centralProcessor;

    @Mock
    private CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Mock
    private CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    @Mock
    private CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    @Mock
    private CpuMonitoringBackupService cpuMonitoringBackupService;

    @Mock
    private MinuteStatDataBufferManager minuteStatDataBufferManager;

    @Mock
    private DateUtil dateUtil;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private ShutdownManager shutdownManager;

    @Mock
    private FormatterConfig formatterConfig;

    @Mock
    private BuildFactory buildFactory;


    @InjectMocks
    private CpuMonitoringManageService cpuMonitoringManageService;

    private MockedStatic<BuildFactory> buildFactoryMockedStatic;

    private DateTimeFormatter formatter;

    @Test
    public void testDateUtilInjection() {
        assertNotNull(dateUtil, "DateUtil should not be null");
    }

    @Test
    public void testTimeProviderInjection() {
        assertNotNull(timeProvider, "TimeProvider should not be null");
    }


    @Test
    public void testShutdownManagerInjection() {
        assertNotNull(shutdownManager, "ShutdownManager should not be null");
    }

    @Test
    public void testBuildFactoryInjection() {
        assertNotNull(buildFactory, "BuildFactory should not be null");
    }

    @Test
    public void testFormatterConfigInjection() {
        assertNotNull(formatterConfig, "FormatterConfig should not be null");
    }


    @BeforeEach
    public void setup() {
        log.info("Calling setup()");
        buildFactoryMockedStatic = mockStatic(BuildFactory.class);
        buildFactoryMockedStatic.when(BuildFactory::getInstance).thenReturn(buildFactory);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    }

    @AfterEach
    public void shutUp() {
        log.info("Calling buildFactoryMockedStatic shutUp");
        buildFactoryMockedStatic.close();
        log.info("Calling shutdownManager shutUp");
        shutdownManager.shutdown();

    }


    protected ZonedDateTime parseZonedDateTime(String time) {
        return ZonedDateTime.parse(time, formatter);

    }


//        @Nested
//    @DisplayName("분 단위 CPU 사용량 저장")
//    class SaveMonitoringCpuUsageTests {
//
//        @BeforeEach
//        void SaveMonitoringCpuUsageTestSetup(){
//            minuteStatDataBufferManager = new MinuteStatDataBufferManager(cpuUsageRateByMinuteRepository);
//        }
//        @Test
//        @DisplayName("분 단위 CPU 사용량 저장 성공")
//        void successSaveMonitoringCpuUsageTest() throws Exception {
//            long[] startTicks = {1L, 2L, 3L, 4L};
//            Double averageCpuUsage = 2.5;
//            Double cpuUsageStat = averageCpuUsage * PERCENTAGE;
//            DecimalFormat roundUsage = new DecimalFormat("0.00");
//            Double cpuUsageResult = Double.parseDouble(roundUsage.format(cpuUsageStat));
//
//            //1번 작업
//            when(centralProcessor.getSystemCpuLoadTicks()).thenReturn(startTicks);
//            //2번
//            when(centralProcessor.getSystemCpuLoadBetweenTicks(eq(startTicks))).thenReturn(averageCpuUsage * PERCENTAGE);
//            //3번
//            CpuUsageRateByMinute mockData = new CpuUsageRateByMinute();
//            mockData.setUsageRate(cpuUsageResult);
//            //4번
//            cpuMonitoringManageService.saveMonitoringCpuUsage();
//
////            when(cpuUsageRateByMinuteRepository.save(captor.capture())).thenReturn(new CpuUsageRateByMinute());
//
//
//            verify(minuteStatDataBufferManager, times(1)).collectCpuUsageRateByMinuteData(mockData);
//            verifyNoMoreInteractions(minuteStatDataBufferManager);
//
//        }
//
//        @Test
//        @DisplayName("분 단위 CPU 사용량 저장 실패-비동기 반복 횟수 전부 소진")
//        void failSaveMonitoringCpuUsageTest() throws Exception {
//            when(centralProcessor.getSystemCpuLoadTicks()).thenReturn(new long[]{1, 2, 3, 4});
//            when(centralProcessor.getSystemCpuLoadBetweenTicks(any(long[].class))).thenThrow(new RuntimeException("Test Exception"));
//            assertThrows(RuntimeException.class, () -> {
//                Future<Void> result = cpuMonitoringManageService.saveMonitoringCpuUsage();
//                result.get();
//            });
//            verify(cpuUsageRateByMinuteRepository, never()).save(any(CpuUsageRateByMinute.class));
//        }
//    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 저장")
    class SaveAverageCpuUsageByHourTests {

        private ZonedDateTime startDay;

        private ZonedDateTime endDay;

        @BeforeEach
        void setupDateData() {
            log.info("SaveAverageCpuUsageByHourTests @BeforeEachStart");
            startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.daysAgo(ONE_DAY)).thenReturn(startDay);
            lenient().when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));

        }

        private List<CpuUsageRateByMinute> commonCondition() {
            CpuUsageRateByMinute firstTestData = createCpuUsageRateByMinute(10.0);
            CpuUsageRateByMinute secondTestData = createCpuUsageRateByMinute(20.0);
            List<CpuUsageRateByMinute> minuteStats = Arrays.asList(firstTestData, secondTestData);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(minuteStats);
            return minuteStats;

        }

        private void commonVerify() {
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).daysAgo(ONE_DAY);
            verify(cpuUsageRateByMinuteRepository).findByCreateTimeBetween(startDay, endDay);

        }

        private DoubleSummaryStatistics getDoubleSummaryStatistics(List<CpuUsageRateByMinute> minuteStats) {
            DoubleSummaryStatistics stats = minuteStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
            return stats;

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 저장 성공 -save")
        void successSaveAverageCpuUsageByHour() throws Exception {
            List<CpuUsageRateByMinute> minuteStats = commonCondition();
            DoubleSummaryStatistics stats = getDoubleSummaryStatistics(minuteStats);
            double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
            double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
            double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());
            log.info("Test findByCreateTimeBetween Result:{}", minuteStats);
            log.info("Test getAverage:{}", averageUsage);
            log.info("Test minimumUsage:{}", minimumUsage);
            log.info("Test maximumUsage:{}", maximumUsage);
            CpuUsageRateByHour minuteStatsSteamResult = new CpuUsageRateByHour(null, averageUsage, minimumUsage, maximumUsage);
            when(buildFactory.toBuildByCpuUsageRateByHour(eq(averageUsage), eq(minimumUsage), eq(maximumUsage)))
                    .thenReturn(minuteStatsSteamResult);
            ArgumentCaptor<CpuUsageRateByHour> captor = ArgumentCaptor.forClass(CpuUsageRateByHour.class);
            when(cpuUsageRateByHourRepository.save(captor.capture())).thenReturn(minuteStatsSteamResult);
            log.info("resultDb Test:{}", minuteStatsSteamResult);

            Future<Void> result = cpuMonitoringManageService.saveAverageCpuUsageByHour();
            result.get();

            commonVerify();
            verify(buildFactory).toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);
            verify(cpuUsageRateByHourRepository).save(minuteStatsSteamResult);

        }

        private CpuUsageRateByMinute createCpuUsageRateByMinute(Double tempData) {
            CpuUsageRateByMinute cpuUsageRateByMinute = new CpuUsageRateByMinute();
            double data = formatterConfig.changeDecimalFormatCpuUsage(tempData);
            cpuUsageRateByMinute.setUsageRate(formatterConfig.changeDecimalFormatCpuUsage(data));
            return cpuUsageRateByMinute;

        }


        @Test
        @DisplayName("시 단위 CPU 사용량 - Between result isEmpty")
        void betweenMethodResultIsEmpty() throws Exception {
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween
                    (startDay, endDay)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByHour();
            });

            log.info("NOT_FOUND_DATE.getMessage():{}", NOT_FOUND_DATA.getMessage());
            log.info("exception.getMessage()):{}", exception.getMessage());
            commonVerify();
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage());

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 - save fail")
        void failJpaSaveTest() throws Exception {
            commonCondition();
            ArgumentCaptor<CpuUsageRateByHour> captor = ArgumentCaptor.forClass(CpuUsageRateByHour.class);
            when(cpuUsageRateByHourRepository.save(captor.capture()))
                    .thenThrow(new ProcessCustomException(NOT_FOUND_DATA));

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByHour();
            });

            verify(cpuUsageRateByHourRepository).save(captor.capture());
            commonVerify();
            verify(cpuUsageRateByHourRepository).save(captor.capture());
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage());

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 저장")
    class SaveAverageCpuUsageByDayTests {

        private ZonedDateTime startDay;

        private ZonedDateTime endDay;

        @BeforeEach
        private void setupDateData() {
            log.info("SaveAverageCpuUsageByDayTests @BeforeEachStart");
            startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.getSearchDay(ONE_DAY)).thenReturn(startDay);
            lenient().when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));
        }




        @Test
        @DisplayName("일 단위 CPU 사용량 저장 성공")
        void successSaveAverageCpuUsageByDayTest() throws Exception {
            //준비
            List<CpuUsageRateByHour> cpuUsageStats = commonCondition();
            double resultAverage = formatterConfig.changeDecimalFormatCpuUsage(cpuUsageStats.stream().mapToDouble(CpuUsageRateByHour::getAverage).summaryStatistics().getAverage());
            double resultMin = formatterConfig.changeDecimalFormatCpuUsage(cpuUsageStats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage).summaryStatistics().getMin());
            double resultMax = formatterConfig.changeDecimalFormatCpuUsage(cpuUsageStats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage).summaryStatistics().getMax());
            log.info("resultAverageValue : {}", resultAverage);
            log.info("resultResultMinValue :{}", resultMin);
            log.info("resultResultMaxValue :{}", resultMax);
            CpuUsageRateByDay resultStat = new CpuUsageRateByDay(null, resultAverage, resultMax, resultMin);
            log.info("resultStatValue: {}", resultStat);
            when(buildFactory.toBuildByCpuUsageRateByDay(resultAverage, resultMin, resultMax)).thenReturn(resultStat);
            ArgumentCaptor<CpuUsageRateByDay> captor = ArgumentCaptor.forClass(CpuUsageRateByDay.class);
            when(cpuUsageRateByDayRepository.save(captor.capture())).thenReturn(new CpuUsageRateByDay());

            Future<Void> result = cpuMonitoringManageService.saveAverageCpuUsageByDay();
            result.get();

            commonVerify();
            verify(buildFactory).toBuildByCpuUsageRateByDay(resultAverage, resultMin, resultMax);
            verify(cpuUsageRateByDayRepository).save(any(CpuUsageRateByDay.class));

        }

        private List<CpuUsageRateByHour> commonCondition() {
            CpuUsageRateByHour firstTestData = toBuildByCpuUsageRateByHour(50.00, 20.00, 70.00);
            CpuUsageRateByHour secondTestData = toBuildByCpuUsageRateByHour(30.00, 10.00, 60.00);
            List<CpuUsageRateByHour> cpuUsageStats = Arrays.asList(firstTestData, secondTestData);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(cpuUsageStats);
            return cpuUsageStats;

        }

        private void commonVerify() {
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).getSearchDay(ONE_DAY);
            verify(cpuUsageRateByHourRepository).findByCreateTimeBetween(startDay, endDay);

        }

        private CpuUsageRateByHour toBuildByCpuUsageRateByHour(Double average, Double min, Double max) {
            CpuUsageRateByHour dataForSearch = new CpuUsageRateByHour();
            dataForSearch.setMinimumUsage(formatterConfig.changeDecimalFormatCpuUsage(min));
            dataForSearch.setMaximumUsage(formatterConfig.changeDecimalFormatCpuUsage(max));
            dataForSearch.setAverage(formatterConfig.changeDecimalFormatCpuUsage(average));
            return dataForSearch;
        }

        @Test
        @DisplayName("일 단위 CPU 사용량 저장 - Between result isEmpty")
        void betweenMethodResultIsEmpty() throws Exception {
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByDay();
            });

            commonVerify();
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지않습니다.");
        }

        @Test
        @DisplayName("일 단위 CPU 사용량 저장 - save fail")
        void failJpaSaveTest() {
            commonCondition();
            ArgumentCaptor<CpuUsageRateByDay> captor = ArgumentCaptor.forClass(CpuUsageRateByDay.class);
            when(cpuUsageRateByDayRepository.save(captor.capture())).thenThrow(new ProcessCustomException(NOT_FOUND_DATA));

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByDay().get();
            });

            commonVerify();
            verify(cpuUsageRateByDayRepository).save(captor.capture());
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지않습니다.");

        }

    }

    @Nested
    @DisplayName("분 단위 CPU 사용량 통계 소프트 삭제")
    class SoftDeleteCpuUsageStatsByMinuteTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            pastDay = parseZonedDateTime("2024-06-01T01:00:00.000+09:00");
            when(dateUtil.getSearchDay(ONE_WEEK)).thenReturn(pastDay);
            when(cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay)).thenReturn(2L);

        }

        void verifyTotalBasicVerify() {
            verify(dateUtil).getSearchDay(ONE_WEEK);
            verify(cpuUsageRateByMinuteRepository).softDeleteOldData(pastDay);
            verify(cpuUsageRateByMinuteRepository).findByFlag(DELETE_FLAG);
        }

        @Test
        @DisplayName("분 단위 CPU 사용량 소프트 삭제 성공")
        void successSoftDeleteCpuUsageStatsByMinuteTest() throws Exception {
            List<CpuUsageRateByMinute> oldData = Arrays.asList(new CpuUsageRateByMinute(), new CpuUsageRateByMinute());
            when(cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);

            Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupCpuUsageStatsByMinute();
            future.get();

            verifyTotalBasicVerify();
            verify(cpuMonitoringBackupService).backupCpuUsageStatsByMinute(oldData);

        }

        @Test
        @DisplayName("분 단위 CPU 사용량 소프트 삭제 실패 - findByFlag isEmpty")
        void failSoftDeleteCpuUsageStatsByMinuteTest() throws Exception {
            when(cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupCpuUsageStatsByMinute();
                future.get();
            });

            verifyTotalBasicVerify();
            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByMinute(any());
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외 메세지가 일치하지 않습니다.");

        }

    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 통계 소프트 삭제")
    class SoftDeleteCpuUsageStatsByHourTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.info("SoftDeleteCpuUsageStatsByHourTests @BeforeEachStart");
            pastDay = parseZonedDateTime("2024-03-01T01:00:00.000+09:00");
            when(dateUtil.getSearchMonth(THREE_MONTH)).thenReturn(pastDay);
            when(cpuUsageRateByHourRepository.softDeleteOldData(pastDay)).thenReturn(2L);

        }

        private void verifyTotalBasicVerify() {
            verify(dateUtil).getSearchMonth(THREE_MONTH);
            verify(cpuUsageRateByHourRepository).findByFlag(DELETE_FLAG);

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 통계 소프트 성공")
        void successSoftDeleteCpuUsageStatsByHourTest() throws Exception {
            List<CpuUsageRateByHour> oldData = Arrays.asList(new CpuUsageRateByHour(), new CpuUsageRateByHour());
            when(cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);

            Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByHour();
            future.get();
            ArgumentCaptor<ZonedDateTime> dateCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);

            verifyTotalBasicVerify();
            verify(cpuUsageRateByHourRepository).softDeleteOldData(dateCaptor.capture());
            verify(cpuMonitoringBackupService).backupCpuUsageStatsByHour(oldData);
            assertEquals(pastDay, dateCaptor.getValue(), "올바른 날짜 데이터가 입력되지 않아 테스트에 실패했습니다.");
        }

        @Test
        @DisplayName("시 단위 CPU 사용량 통계 소프트 실패 - findByFlag isEmpty")
        void failFindByFlagResultIsEmpty() throws Exception {
            when(cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByHour();
                future.get();
            });

            verifyTotalBasicVerify();
            verify(cpuUsageRateByHourRepository).softDeleteOldData(pastDay);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지 않습니다.");

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 통계 소프트 삭제")
    class SoftDeleteCpuUsageStatsByDayTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.info("SoftDeleteCpuUsageStatsByDayTests @BeforeEachStart");
            pastDay = parseZonedDateTime("2024-03-01T01:00:00.000+09:00");
            when(dateUtil.getSearchYear(ONE_YEAR)).thenReturn(pastDay);
            when(cpuUsageRateByDayRepository.softDeleteOldData(pastDay)).thenReturn(2L);

        }

        private void verifyTotalBasicVerify() {
            verify(dateUtil).getSearchYear(ONE_YEAR);
            verify(cpuUsageRateByDayRepository).findByFlag(DELETE_FLAG);

        }

        @Test
        @DisplayName("일 단위 CPU 사용량 통계 소프트 성공")
        void successSoftDeleteCpuUsageStatsByDayTest() throws Exception {
            List<CpuUsageRateByDay> oldData = Arrays.asList(new CpuUsageRateByDay(), new CpuUsageRateByDay());
            when(cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);

            Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByDay();
            future.get();
            ArgumentCaptor<ZonedDateTime> dateCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);

            verifyTotalBasicVerify();
            verify(cpuUsageRateByDayRepository).softDeleteOldData(dateCaptor.capture());
            verify(cpuMonitoringBackupService).backupCpuUsageStatsByDay(oldData);
            assertEquals(pastDay, dateCaptor.getValue(), "올바른 날짜 데이터가 입력되지 않아 테스트에 실패했습니다.");

        }

        @Test
        @DisplayName("일 단위 CPU 사용량 통계 소프트 실패 - findByFlag isEmpty")
        void failSoftDeleteCpuUsageStatsByDayTest() {
            when(cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByDay();
                future.get();
            });

            ArgumentCaptor<ZonedDateTime> dateCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);

            verifyTotalBasicVerify();
            verify(cpuUsageRateByDayRepository).softDeleteOldData(dateCaptor.capture());
            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByDay(any());
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지 않습니다");

        }

    }

}


