package com.tera.pretest.cpumonitoring.service.unit;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.DefaultThreadConfig;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.manager.MinuteStatDataBufferManager;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.monitoring.service.DefaultCpuMonitoringManageService;
import com.tera.pretest.core.util.ProviderDateUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import oshi.hardware.CentralProcessor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static com.tera.pretest.core.constant.MonitoringConstant.*;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.*;
import static com.tera.pretest.cpumonitoring.core.constant.TestConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/*Unit Test DefaultCpuMonitoringManageServiceTest
 *Line coverage 83%
 *Class coverage 100%
 *Method coverage 73%
 * */


@Log4j2
@DisplayName("DefaultCpuMonitoringManageService Tests")
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
    private ShutdownManager shutdownManager;

    @Mock
    private FormatterConfig formatterConfig;

    @Mock
    private BuildFactory buildFactory;

    @Mock
    private DefaultThreadConfig threadConfig;

    @Spy
    @InjectMocks
    private DefaultCpuMonitoringManageService cpuMonitoringManageService;

    private DateTimeFormatter formatter;

    @BeforeEach
    public void setup() {
        log.debug("Calling setup()");
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    }

    @AfterEach
    public void shutUp() {
        log.debug("Calling shutUp");
        shutdownManager.shutdown();

    }


    protected ZonedDateTime parseZonedDateTime(String time) {
        return ZonedDateTime.parse(time, formatter);
    }


    @Nested
    @DisplayName("분 단위 CPU 사용량 저장")
    class SaveMonitoringCpuUsageTests {

        @Test
        @DisplayName("분 단위 CPU 사용량 저장 - Tick 계산")
        void SuccessGetTickData() throws InterruptedException {
            long[] startTicks = {1L, 2L, 3L, 4L};
            double averageCpuUsage = 2.5;
            double averageCpuUsagePercentage = averageCpuUsage * PERCENTAGE;
            CpuUsageRateByMinute insertData = new CpuUsageRateByMinute();

            doNothing().when(threadConfig).sleepThread(TEN_SECOND);
            when(centralProcessor.getSystemCpuLoadTicks()).thenReturn(startTicks);
            when(centralProcessor.getSystemCpuLoadBetweenTicks(startTicks)).thenReturn(averageCpuUsage);
            when(formatterConfig.changeDecimalFormatCpuUsage(any(Double.class))).thenReturn(averageCpuUsagePercentage);
            when(buildFactory.toBuildByCpuUsageRateByMinute(any(Double.class))).thenReturn(insertData);
            cpuMonitoringManageService.saveMonitoringCpuUsage();

            verify(threadConfig, times(ONE_MINUTE_COUNT_BY_SEC)).sleepThread(TEN_SECOND);
            verify(centralProcessor, times(ONE_MINUTE_COUNT_BY_SEC)).getSystemCpuLoadTicks();
            verify(centralProcessor, times(ONE_MINUTE_COUNT_BY_SEC)).getSystemCpuLoadBetweenTicks(startTicks);
            verify(formatterConfig, times(ONE_MINUTE_COUNT_BY_SEC)).changeDecimalFormatCpuUsage(anyDouble());
            verify(minuteStatDataBufferManager).collectCpuUsageRateByMinuteData(insertData);

        }


        @Test
        @DisplayName("분 단위 CPU 사용량 저장 실패 - InterruptedException 발생 ")
        void successCollectCpuUsageRateByMinuteData() throws Exception {
            doThrow(new InterruptedException()).when(threadConfig).sleepThread(TEN_SECOND);

            InterruptedException exception = assertThrows(InterruptedException.class,
                    () -> threadConfig.sleepThread(TEN_SECOND));

            assertEquals(InterruptedException.class, exception.getClass(), NOT_MATCH_EXCEPTION);

        }

    }


    @Nested
    @DisplayName("시 단위 CPU 사용량 정보 수집")
    class SaveAverageCpuUsageByHourTests {

        private ZonedDateTime startDay;

        private ZonedDateTime endDay;

        @BeforeEach
        void setupDateData() {
            log.debug("Calling setupDateData");
            startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.daysAgo(ONE_DAY)).thenReturn(startDay);
            lenient().when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));

        }

        private List<CpuUsageRateByMinute> commonCondition() {
            log.debug("Calling commonCondition");
            CpuUsageRateByMinute firstTestData = createCpuUsageRateByMinute(10.0);
            CpuUsageRateByMinute secondTestData = createCpuUsageRateByMinute(20.0);
            List<CpuUsageRateByMinute> minuteStats = Arrays.asList(firstTestData, secondTestData);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(minuteStats);
            return minuteStats;

        }

        private void commonVerify() {
            log.debug("Calling commonVerify");
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).daysAgo(ONE_DAY);
            verify(cpuUsageRateByMinuteRepository).findByCreateTimeBetween(startDay, endDay);

        }

        private DoubleSummaryStatistics getDoubleSummaryStatistics(List<CpuUsageRateByMinute> minuteStats) {
            return  minuteStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();

        }

        @Test
        @DisplayName("성공")
        void successCollectingAverageCpuUsageByMinuteStat()  {
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
        @DisplayName("실패 - Between result isEmpty")
        void betweenMethodResultIsEmpty()  {
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween
                    (startDay, endDay)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () ->
                    cpuMonitoringManageService.saveAverageCpuUsageByHour());

            commonVerify();
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 정보 저장")
    class saveOneHourCpuUsageStatsToDbTests {

        @Test
        @DisplayName("성공")
        void successSaveAverageCpuUsageByHourStat()  {
            CpuUsageRateByHour cpuUsageStat = new CpuUsageRateByHour();
            when(cpuUsageRateByHourRepository.save(cpuUsageStat)).thenReturn(cpuUsageStat);

            cpuMonitoringManageService.saveOneHourCpuUsageStatsToDb(cpuUsageStat);

            verify(cpuUsageRateByHourRepository).save(cpuUsageStat);
        }

        @Test
        @DisplayName("실패 - 예외 처리")
        void failSaveAverageCpuUsageByHourStat()  {
            CpuUsageRateByHour cpuUsageStat = new CpuUsageRateByHour();
            when(cpuUsageRateByHourRepository.save(any(CpuUsageRateByHour.class))).thenThrow(new ProcessCustomException(NOT_FOUND_DATA));

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () ->
                    cpuMonitoringManageService.saveOneHourCpuUsageStatsToDb(cpuUsageStat));

            verify(cpuUsageRateByHourRepository).save(cpuUsageStat);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }


    @Nested
    @DisplayName("일 단위 CPU 사용량 통계 수집")
    class SaveAverageCpuUsageByDayTests {

        private ZonedDateTime startDay;

        private ZonedDateTime endDay;

        @BeforeEach
        public void setupDateData() {
            log.debug("Calling setupDateData");
            startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.getSearchDay(ONE_DAY)).thenReturn(startDay);
            lenient().when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));

        }

        private void commonVerify() {
            log.debug("Calling commonVerify");
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).getSearchDay(ONE_DAY);
            verify(cpuUsageRateByHourRepository).findByCreateTimeBetween(startDay, endDay);

        }


        @Test
        @DisplayName("일 단위 CPU 사용량 통계 수집 - 성공")
        void successCollectingAverageCpuUsageByMinuteStat()  {
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
            log.debug("Calling commonCondition");
            CpuUsageRateByHour firstTestData = toBuildByCpuUsageRateByHour(50.00, 20.00, 70.00);
            CpuUsageRateByHour secondTestData = toBuildByCpuUsageRateByHour(30.00, 10.00, 60.00);
            List<CpuUsageRateByHour> cpuUsageStats = Arrays.asList(firstTestData, secondTestData);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(cpuUsageStats);
            return cpuUsageStats;

        }

        private CpuUsageRateByHour toBuildByCpuUsageRateByHour(Double average, Double min, Double max) {
            log.debug("Calling toBuildByCpuUsageRateByHour average:{}, min:{}, max:{}", average, min, max);
            CpuUsageRateByHour dataForSearch = new CpuUsageRateByHour();
            dataForSearch.setMinimumUsage(formatterConfig.changeDecimalFormatCpuUsage(min));
            dataForSearch.setMaximumUsage(formatterConfig.changeDecimalFormatCpuUsage(max));
            dataForSearch.setAverage(formatterConfig.changeDecimalFormatCpuUsage(average));
            return dataForSearch;

        }

        @Test
        @DisplayName("일 단위 CPU 사용량 통계 수집 - Between isEmpty")
        void betweenMethodResultIsEmpty() {
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(Collections.emptyList());

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () ->
                    cpuMonitoringManageService.saveAverageCpuUsageByDay());

            commonVerify();
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }


    @Nested
    @DisplayName("일 단위 CPU 사용량 정보 저장")
    class saveOneDayCpuUsageStatsToDbTests {

        @Test
        @DisplayName("성공")
        void successSaveAverageCpuUsageByDayStat() {
            CpuUsageRateByDay cpuUsageStat = new CpuUsageRateByDay();

            when(cpuUsageRateByDayRepository.save(any(CpuUsageRateByDay.class))).thenReturn(cpuUsageStat);
            cpuMonitoringManageService.saveOneDayCpuUsageStatsToDb(cpuUsageStat);

            verify(cpuUsageRateByDayRepository).save(cpuUsageStat);

        }

        @Test
        @DisplayName("실패 - 예외 처리")
        void failSaveAverageCpuUsageByDayStat() {
            CpuUsageRateByDay cpuUsageStat = new CpuUsageRateByDay();

            when(cpuUsageRateByDayRepository.save(cpuUsageStat)).thenThrow(new ProcessCustomException(NOT_FOUND_DATA));
            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () ->
                    cpuMonitoringManageService.saveOneDayCpuUsageStatsToDb(cpuUsageStat));

            verify(cpuUsageRateByDayRepository).save(cpuUsageStat);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }
    }


    @Nested
    @DisplayName("분 단위 CPU 사용량 통계 소프트 딜리트")
    class softDeleteStatsByHouTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.debug("Calling setupDateData");
            pastDay = parseZonedDateTime("2024-06-01T01:00:00.000+09:00");
            when(dateUtil.getSearchDay(ONE_WEEK)).thenReturn(pastDay);
        }

        void commonVerify() {
            log.debug("Calling commonVerify");
            verify(dateUtil).getSearchDay(ONE_WEEK);
            verify(cpuUsageRateByMinuteRepository).softDeleteOldData(pastDay);
        }

        @Test
        @DisplayName("성공")
        void successSoftDeleteStatsByMinute() {
            int tempSoftDeleteResult = 60;
            when(cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay)).thenReturn(tempSoftDeleteResult);

            cpuMonitoringManageService.softDeleteStatsByMinute();

            commonVerify();
            assertEquals(tempSoftDeleteResult, cpuUsageRateByMinuteRepository.softDeleteOldData(pastDay), NOT_MATCH_VALUE);

        }

        @Test
        @DisplayName("실패 - DataAccessException")
        void failSoftDeleteStatsByMinute() {
            doThrow(new DataAccessException(RELATED_DB_EXCEPTION) {
            }).when(cpuUsageRateByMinuteRepository).softDeleteOldData(pastDay);

            DataAccessException exception = assertThrows(DataAccessException.class, () ->
                    cpuMonitoringManageService.softDeleteAndBackupCpuUsageStatsByMinute());

            commonVerify();
            assertEquals(RELATED_DB_EXCEPTION, exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }


    @Nested
    @DisplayName("분 단위 CPU 사용량 백업")
    class BackupCpuUsageStatsByMinuteTests {

        @Test
        @DisplayName("성공")
        void successBackupCpuUsageStatsByMinute() {
            List<CpuUsageRateByMinute> oldData = Arrays.asList(new CpuUsageRateByMinute(), new CpuUsageRateByMinute());
            when(cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);

            cpuMonitoringManageService.backupCpuUsageStatsByMinute();

            verify(cpuUsageRateByMinuteRepository).findByFlag(DELETE_FLAG);
            verify(cpuMonitoringBackupService).backupCpuUsageStatsByMinute(oldData);

        }

        @Test
        @DisplayName("실패 - findByFlag isEmpty")
        void failBackupCpuUsageStatsByMinute() {
            List<CpuUsageRateByMinute> empty = Collections.emptyList();

            when(cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG)).thenReturn(empty);
            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () ->
                    cpuMonitoringManageService.backupCpuUsageStatsByMinute());

            verify(cpuUsageRateByMinuteRepository).findByFlag(DELETE_FLAG);
            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByMinute(empty);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }


    @Nested
    @DisplayName("시 단위 CPU 사용량 통계 소프트 딜리트")
    class softDeleteStatsByHourTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.debug("Calling setupDateData");
            pastDay = parseZonedDateTime("2024-03-01T01:00:00.000+09:00");
            when(dateUtil.getSearchMonth(THREE_MONTH)).thenReturn(pastDay);

        }

        private void commonVerify() {
            log.debug("Calling commonVerify");
            verify(dateUtil).getSearchMonth(THREE_MONTH);

        }

        @Test
        @DisplayName("성공")
        void successSoftDeleteCpuUsageStatsByHourTest()  {
            int tempSoftDeleteResult = 24;
            when(cpuUsageRateByHourRepository.softDeleteOldData(pastDay)).thenReturn(tempSoftDeleteResult);

            cpuMonitoringManageService.softDeleteStatsByHour();

            verify(dateUtil).getSearchMonth(THREE_MONTH);
            verify(cpuUsageRateByHourRepository).softDeleteOldData(pastDay);
            assertEquals(tempSoftDeleteResult, cpuUsageRateByHourRepository.softDeleteOldData(pastDay), NOT_MATCH_VALUE);
        }

        @Test
        @DisplayName("실패 - DataAccessException")
        void failFindByFlagResultIsEmpty()  {
            doThrow(new DataAccessException(RELATED_DB_EXCEPTION) {
            }).when(cpuUsageRateByHourRepository).softDeleteOldData(pastDay);

            DataAccessException exception = assertThrows(DataAccessException.class, () -> cpuMonitoringManageService.softDeleteStatsByHour());

            commonVerify();
            verify(cpuUsageRateByHourRepository).softDeleteOldData(pastDay);
            assertEquals(RELATED_DB_EXCEPTION, exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);
        }

    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 통계 백업")
    class BackupCpuUsageStatsByDayTests {

        @Test
        @DisplayName("성공")
        void successSoftDeleteCpuUsageStatsByHourTest() {
            List<CpuUsageRateByHour> oldData = Arrays.asList(new CpuUsageRateByHour(), new CpuUsageRateByHour());
            when(cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);

            cpuMonitoringManageService.backupCpuUsageStatsByHour();

            verify(cpuUsageRateByHourRepository).findByFlag(DELETE_FLAG);
            verify(cpuMonitoringBackupService).backupCpuUsageStatsByHour(oldData);

        }

        @Test
        @DisplayName("실패 - findByFlag isEmpty")
        void failFindByFlagResultIsEmpty() {
            List<CpuUsageRateByHour> empty = Collections.emptyList();
            when(cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG)).thenReturn(empty);

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () ->
                cpuMonitoringManageService.backupCpuUsageStatsByHour());

            verify(cpuUsageRateByHourRepository).findByFlag(DELETE_FLAG);
            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByHour(empty);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 통계 소프트 딜리트")
    class SoftDeleteStatsByDayTests {

        private ZonedDateTime pastDay;

        @BeforeEach
        void setupDateData() {
            log.debug("Calling setupDateData");
            pastDay = parseZonedDateTime("2024-03-01T01:00:00.000+09:00");
            when(dateUtil.getSearchYear(ONE_YEAR)).thenReturn(pastDay);

        }

        private void commonVerify() {
            log.debug("Calling commonVerify");
            verify(dateUtil).getSearchYear(ONE_YEAR);

        }

        @Test
        @DisplayName("성공")
        void successSoftDeleteCpuUsageStatsByDayTest() {
            int tempSoftDeleteResult = 30;

            when(cpuUsageRateByDayRepository.softDeleteOldData(pastDay)).thenReturn(30); // 30 or 31
            cpuMonitoringManageService.softDeleteStatsByDay();

            verify(cpuUsageRateByDayRepository).softDeleteOldData(pastDay);
            assertEquals(tempSoftDeleteResult, cpuUsageRateByDayRepository.softDeleteOldData(pastDay), NOT_MATCH_VALUE);
            commonVerify();
        }

        @Test
        @DisplayName("실패 - DataAccessException")
        void failSoftDeleteCpuUsageStatsByDayTest() {
            doThrow(new DataAccessException(RELATED_DB_EXCEPTION) {
            }).when(cpuUsageRateByDayRepository).softDeleteOldData(pastDay);

            DataAccessException exception = assertThrows(DataAccessException.class, () -> cpuMonitoringManageService.softDeleteStatsByDay());

            commonVerify();
            verify(cpuUsageRateByDayRepository).softDeleteOldData(pastDay);
            assertEquals(RELATED_DB_EXCEPTION, exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }


    @Nested
    @DisplayName("일 단위 CPU 사용량 통계 백업")
    class SoftDeleteCpuUsageStatsByDayTests {

        private void commonVerify() {
            log.debug("Calling commonVerify");
            verify(cpuUsageRateByDayRepository).findByFlag(DELETE_FLAG);
        }

        @Test
        @DisplayName("성공")
        void successSoftDeleteCpuUsageStatsByDayTest()  {
            List<CpuUsageRateByDay> oldData = Arrays.asList(new CpuUsageRateByDay(), new CpuUsageRateByDay());
            when(cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);

            cpuMonitoringManageService.backupCpuUsageStatsByDay();

            commonVerify();
            verify(cpuMonitoringBackupService).backupCpuUsageStatsByDay(oldData);
        }

        @Test
        @DisplayName("실패 - findByFlag isEmpty")
        void failSoftDeleteCpuUsageStatsByDayTest() {
            List<CpuUsageRateByDay> empty = Collections.emptyList();
            when(cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG)).thenReturn(empty);

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> cpuMonitoringManageService.backupCpuUsageStatsByDay());

            commonVerify();
            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByDay(empty);
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);
        }

    }

}


