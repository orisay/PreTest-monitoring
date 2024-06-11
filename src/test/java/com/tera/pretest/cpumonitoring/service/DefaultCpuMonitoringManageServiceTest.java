package com.tera.pretest.cpumonitoring.service;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.manager.MinuteStatDataBufferManager;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.monitoring.service.DefaultCpuMonitoringManageService;

import com.tera.pretest.core.util.ProviderDateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import oshi.hardware.CentralProcessor;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;

import static com.tera.pretest.core.contant.MonitoringConstant.*;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Log4j2
@DisplayName("DefaultCpuMonitoringManageService Test")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Import({ZonedDateTimeFormatConfig.class, DefaultCpuMonitoringManageService.class})
public class DefaultCpuMonitoringManageServiceTest {

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
    private ProviderDateUtil dateUtil;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private ShutdownManager shutdownManager;

    @Mock
    private FormatterConfig formatterConfig;

    @Mock
    private BuildFactory buildFactory;

    @Spy
    @InjectMocks
    private DefaultCpuMonitoringManageService cpuMonitoringManageService;

    private MockedStatic<BuildFactory> buildFactoryMockedStatic;

    private DateTimeFormatter formatter;

    @BeforeEach
    public void setup() {
        log.info("Calling setup()");
        buildFactoryMockedStatic = mockStatic(BuildFactory.class);
        buildFactoryMockedStatic.when(BuildFactory::getInstance).thenReturn(buildFactory);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    }

    @AfterEach
    public void shutUp() {
        log.info("Calling shutUp");
        buildFactoryMockedStatic.close();
        shutdownManager.shutdown();

    }


    protected ZonedDateTime parseZonedDateTime(String time) {
        return ZonedDateTime.parse(time, formatter);

    }


    @Nested
    @DisplayName("분 단위 CPU 사용량 저장")
    class SaveMonitoringCpuUsageTests {

        /*
        getAverageCpuUsageByOneMinute 통해
        10초마다 1분간 데이터를 수집해서 통계를 내기에 호출은 6번 됩니다.
        */
        @Test
        @DisplayName("분 단위 CPU 사용량 저장 - Tick 계산")
        void SuccessGetTickData() throws InterruptedException {
            long[] startTicks = {1L, 2L, 3L, 4L};
            double averageCpuUsage = 2.5;
            double averageCpuUsagePercentage = averageCpuUsage * PERCENTAGE;
            CpuUsageRateByMinute insertData = new CpuUsageRateByMinute();

            doNothing().when(cpuMonitoringManageService).threadSleep(TEN_SECOND);
            when(centralProcessor.getSystemCpuLoadTicks()).thenReturn(startTicks);
            when(centralProcessor.getSystemCpuLoadBetweenTicks(startTicks)).thenReturn(averageCpuUsage);
            when(formatterConfig.changeDecimalFormatCpuUsage(any(Double.class))).thenReturn(averageCpuUsagePercentage);
            when(buildFactory.toBuildByCpuUsageRateByMinute(any(Double.class))).thenReturn(insertData);
            cpuMonitoringManageService.saveMonitoringCpuUsage();

            verify(cpuMonitoringManageService, times(ONE_MINUTE_COUNT_BY_SEC)).threadSleep(TEN_SECOND);
            verify(centralProcessor, times(ONE_MINUTE_COUNT_BY_SEC)).getSystemCpuLoadTicks();
            verify(centralProcessor, times(ONE_MINUTE_COUNT_BY_SEC)).getSystemCpuLoadBetweenTicks(startTicks);
            verify(formatterConfig, times(ONE_MINUTE_COUNT_BY_SEC)).changeDecimalFormatCpuUsage(anyDouble());
            verify(minuteStatDataBufferManager).collectCpuUsageRateByMinuteData(insertData);

        }


        @Test
        @DisplayName("분 단위 CPU 사용량 저장 실패 - InterruptedException 발생 ")
        void successCollectCpuUsageRateByMinuteData() throws Exception {
            doThrow(new InterruptedException()).when(cpuMonitoringManageService).threadSleep(TEN_SECOND);
            InterruptedException exception = assertThrows(InterruptedException.class, () -> {
                cpuMonitoringManageService.threadSleep(TEN_SECOND);
            });

            assertEquals(InterruptedException.class, exception.getClass(), "예외가 일치하지 않습니다.");

        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 정보 수집")
    class SaveAverageCpuUsageByHourTests {

        private ZonedDateTime startDay;

        private ZonedDateTime endDay;

        @BeforeEach
        void setupDateData() {
            log.info("Calling setupDateData");
            startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.daysAgo(ONE_DAY)).thenReturn(startDay);
            lenient().when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));

        }

        private List<CpuUsageRateByMinute> commonCondition() {
            log.info("Calling commonCondition");
            CpuUsageRateByMinute firstTestData = createCpuUsageRateByMinute(10.0);
            CpuUsageRateByMinute secondTestData = createCpuUsageRateByMinute(20.0);
            List<CpuUsageRateByMinute> minuteStats = Arrays.asList(firstTestData, secondTestData);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(minuteStats);
            return minuteStats;

        }

        private void commonVerify() {
            log.info("Calling commonVerify");
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).daysAgo(ONE_DAY);
            verify(cpuUsageRateByMinuteRepository).findByCreateTimeBetween(startDay, endDay);

        }

        private DoubleSummaryStatistics getDoubleSummaryStatistics(List<CpuUsageRateByMinute> minuteStats) {
            DoubleSummaryStatistics stats = minuteStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
            return stats;

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 통계 수집 - 성공")
        void successCollectingAverageCpuUsageByMinuteStat() throws Exception {
            List<CpuUsageRateByMinute> minuteStats = commonCondition();
            DoubleSummaryStatistics stats = getDoubleSummaryStatistics(minuteStats);
            double averageUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getAverage());
            double minimumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMin());
            double maximumUsage = formatterConfig.changeDecimalFormatCpuUsage(stats.getMax());

            CpuUsageRateByHour minuteStatsSteamResult = new CpuUsageRateByHour(null, averageUsage, minimumUsage, maximumUsage);
            when(buildFactory.toBuildByCpuUsageRateByHour(eq(averageUsage), eq(minimumUsage), eq(maximumUsage)))
                    .thenReturn(minuteStatsSteamResult);
            cpuMonitoringManageService.saveAverageCpuUsageByHour();
            commonVerify();
            verify(buildFactory).toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);

        }

        private CpuUsageRateByMinute createCpuUsageRateByMinute(Double tempData) {
            CpuUsageRateByMinute cpuUsageRateByMinute = new CpuUsageRateByMinute();
            double data = formatterConfig.changeDecimalFormatCpuUsage(tempData);
            cpuUsageRateByMinute.setUsageRate(formatterConfig.changeDecimalFormatCpuUsage(data));
            return cpuUsageRateByMinute;

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 통계 수집 - Between result isEmpty")
        void betweenMethodResultIsEmpty() throws Exception {
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween
                    (startDay, endDay)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByHour();
            });

            commonVerify();
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage());

        }

    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 정보 저장")
    class saveOneHourCpuUsageStatsToDbTests {
        @Test
        @DisplayName("시 단위 CPU 사용량 통계 저장 - 성공")
        void successSaveAverageCpuUsageByHourStat() throws Exception {
            CpuUsageRateByHour cpuUsageStat = new CpuUsageRateByHour();
            when(cpuUsageRateByHourRepository.save(cpuUsageStat)).thenReturn(cpuUsageStat);

            cpuMonitoringManageService.saveOneHourCpuUsageStatsToDb(cpuUsageStat);

            verify(cpuUsageRateByHourRepository).save(cpuUsageStat);

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 저장 - 실패")
        void failSaveAverageCpuUsageByHourStat() throws Exception {
            CpuUsageRateByHour cpuUsageStat = new CpuUsageRateByHour();
            when(cpuUsageRateByHourRepository.save(any(CpuUsageRateByHour.class))).thenThrow(new ProcessCustomException(NOT_FOUND_DATA));

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveOneHourCpuUsageStatsToDb(cpuUsageStat);
            });

            verify(cpuUsageRateByHourRepository).save(cpuUsageStat);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지 않습니다.");

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 통계 수집")
    class SaveAverageCpuUsageByDayTests {

        private ZonedDateTime startDay;

        private ZonedDateTime endDay;

        @BeforeEach
        private void setupDateData() {
            log.info("Calling setupDateData");
            startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.getSearchDay(ONE_DAY)).thenReturn(startDay);
            lenient().when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));
        }


        @Test
        @DisplayName("일 단위 CPU 사용량 통계 수집 - 성공")
        void successCollectingAverageCpuUsageByMinuteStat() throws Exception {
            List<CpuUsageRateByHour> cpuUsageStats = commonCondition();
            double resultAverage = formatterConfig.changeDecimalFormatCpuUsage(cpuUsageStats.stream().mapToDouble(CpuUsageRateByHour::getAverage).summaryStatistics().getAverage());
            double resultMin = formatterConfig.changeDecimalFormatCpuUsage(cpuUsageStats.stream().mapToDouble(CpuUsageRateByHour::getMinimumUsage).summaryStatistics().getMin());
            double resultMax = formatterConfig.changeDecimalFormatCpuUsage(cpuUsageStats.stream().mapToDouble(CpuUsageRateByHour::getMaximumUsage).summaryStatistics().getMax());
            CpuUsageRateByDay resultStat = new CpuUsageRateByDay(null, resultAverage, resultMax, resultMin);

            when(buildFactory.toBuildByCpuUsageRateByDay(resultAverage, resultMin, resultMax)).thenReturn(resultStat);
            cpuMonitoringManageService.saveAverageCpuUsageByDay();

            commonVerify();
            verify(buildFactory).toBuildByCpuUsageRateByDay(resultAverage, resultMin, resultMax);

        }

        private List<CpuUsageRateByHour> commonCondition() {
            log.info("Calling commonCondition");
            CpuUsageRateByHour firstTestData = toBuildByCpuUsageRateByHour(50.00, 20.00, 70.00);
            CpuUsageRateByHour secondTestData = toBuildByCpuUsageRateByHour(30.00, 10.00, 60.00);
            List<CpuUsageRateByHour> cpuUsageStats = Arrays.asList(firstTestData, secondTestData);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(cpuUsageStats);
            return cpuUsageStats;

        }

        private void commonVerify() {
            log.info("Calling commonVerify");
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).getSearchDay(ONE_DAY);
            verify(cpuUsageRateByHourRepository).findByCreateTimeBetween(startDay, endDay);

        }

        private CpuUsageRateByHour toBuildByCpuUsageRateByHour(Double average, Double min, Double max) {
            log.info("Calling toBuildByCpuUsageRateByHour average:{}, min:{}, max:{}", average, min, max);
            CpuUsageRateByHour dataForSearch = new CpuUsageRateByHour();
            dataForSearch.setMinimumUsage(formatterConfig.changeDecimalFormatCpuUsage(min));
            dataForSearch.setMaximumUsage(formatterConfig.changeDecimalFormatCpuUsage(max));
            dataForSearch.setAverage(formatterConfig.changeDecimalFormatCpuUsage(average));
            return dataForSearch;
        }

        @Test
        @DisplayName("일 단위 CPU 사용량 통계 수집 - Between isEmpty")
        void betweenMethodResultIsEmpty() throws Exception {
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByDay();
            });

            commonVerify();
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지않습니다.");

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 정보 저장")
    class saveOneDayCpuUsageStatsToDbTests {

        @Test
        @DisplayName("일 단위 CPU 사용량 정보 저장 - 성공")
        void successSaveAverageCpuUsageByDayStat() {
            CpuUsageRateByDay cpuUsageStat = new CpuUsageRateByDay();
            when(cpuUsageRateByDayRepository.save(any(CpuUsageRateByDay.class))).thenReturn(cpuUsageStat);

            cpuMonitoringManageService.saveOneDayCpuUsageStatsToDb(cpuUsageStat);

            verify(cpuUsageRateByDayRepository).save(cpuUsageStat);

        }

        @Test
        @DisplayName("일 단위 CPU 사용량 정보 저장 - 실패")
        void failSaveAverageCpuUsageByDayStat() {
            CpuUsageRateByDay cpuUsageStat = new CpuUsageRateByDay();
            when(cpuUsageRateByDayRepository.save(cpuUsageStat)).thenThrow(new ProcessCustomException(NOT_FOUND_DATA));
            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveOneDayCpuUsageStatsToDb(cpuUsageStat);
            });

            verify(cpuUsageRateByDayRepository).save(cpuUsageStat);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지 않습니다.");

        }


    }

    @Nested
    @DisplayName("분 단위 CPU 사용량 통계 소프트 삭제")
    class SoftDeleteCpuUsageStatsByMinuteTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.info("Calling setupDateData");
            pastDay = parseZonedDateTime("2024-06-01T01:00:00.000+09:00");
            when(dateUtil.getSearchDay(ONE_WEEK)).thenReturn(pastDay);
            when(cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay)).thenReturn(2L);

        }

        void verifyTotalBasicVerify() {
            log.info("Calling verifyTotalBasicVerify");
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
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외가 일치하지 않습니다.");

        }

    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 통계 소프트 삭제")
    class SoftDeleteCpuUsageStatsByHourTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.info("Calling setupDateData");
            pastDay = parseZonedDateTime("2024-03-01T01:00:00.000+09:00");
            when(dateUtil.getSearchMonth(THREE_MONTH)).thenReturn(pastDay);
            when(cpuUsageRateByHourRepository.softDeleteOldData(pastDay)).thenReturn(2L);

        }

        private void verifyTotalBasicVerify() {
            log.info("Calling verifyTotalBasicVerify");
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
            log.info("Calling setupDateData");
            pastDay = parseZonedDateTime("2024-03-01T01:00:00.000+09:00");
            when(dateUtil.getSearchYear(ONE_YEAR)).thenReturn(pastDay);
            when(cpuUsageRateByDayRepository.softDeleteOldData(pastDay)).thenReturn(2L);

        }

        private void verifyTotalBasicVerify() {
            log.info("Calling verifyTotalBasicVerify");
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


