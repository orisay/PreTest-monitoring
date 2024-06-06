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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import oshi.hardware.CentralProcessor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.tera.pretest.core.contant.MonitoringConstant.ONE_DAY;
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
//@ContextConfiguration(classes = {UnitTestConfig.class, AsyncTestConfig.class})
@ContextConfiguration(classes = {UnitTestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private ShutdownManager shutdownManager;

    @Mock
    private FormatterConfig formatterConfig;

    @Mock
    private BuildFactory buildFactory;

    @InjectMocks
    private CpuMonitoringManageService cpuMonitoringManageService;

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
        log.info("Calling shutUp()");
        buildFactoryMockedStatic.close();
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

        private CpuUsageRateByMinute createCpuUsageRateByMinute(Double tempData) {
            log.info("createCpuUsageRateByMinute Start");
            CpuUsageRateByMinute cpuUsageRateByMinute = new CpuUsageRateByMinute();
            double data = formatterConfig.changeDecimalFormatCpuUsage(tempData);
            cpuUsageRateByMinute.setUsageRate(formatterConfig.changeDecimalFormatCpuUsage(data));
            return cpuUsageRateByMinute;
        }


        private void setMockData(ZonedDateTime startDay, ZonedDateTime endDay) {
            log.info("setMockData Start");
            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
            when(dateUtil.daysAgo(ONE_DAY)).thenReturn(startDay);
            when(formatterConfig.changeDecimalFormatCpuUsage(anyDouble())).thenAnswer(invocation -> invocation.getArgument(0));

        }



        private DoubleSummaryStatistics getDoubleSummaryStatistics(List<CpuUsageRateByMinute> minuteStats) {
            log.info("getDoubleSummaryStatistics Start");
            DoubleSummaryStatistics stats = minuteStats.stream().mapToDouble(CpuUsageRateByMinute::getUsageRate).summaryStatistics();
            return stats;
        }

        @Test
        @DisplayName("시 단위 CPU 사용량 저장 성공")
        void successSaveAverageCpuUsageByHourTest() throws Exception {
            //given
            ZonedDateTime startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            ZonedDateTime endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            setMockData(startDay, endDay);
            CpuUsageRateByMinute firstTestData = createCpuUsageRateByMinute(10.0);
            CpuUsageRateByMinute secondTestData = createCpuUsageRateByMinute(20.0);


            List<CpuUsageRateByMinute> minuteStats = Arrays.asList(firstTestData, secondTestData);

            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(minuteStats);
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

            Future<Void> result =  cpuMonitoringManageService.saveAverageCpuUsageByHour();
            result.get();

            //then
            verify(dateUtil).getTodayTruncatedToDay();
            verify(dateUtil).daysAgo(ONE_DAY);
            verify(cpuUsageRateByMinuteRepository).findByCreateTimeBetween(startDay, endDay);
            verify(buildFactory).toBuildByCpuUsageRateByHour(averageUsage, minimumUsage, maximumUsage);
            verify(cpuUsageRateByHourRepository).save(minuteStatsSteamResult);

        }



        @Test
        @DisplayName("시 단위 CPU 사용량 - Between result isEmpty")
        void failSaveAverageCpuUsageByHourTest() throws Exception {
            ZonedDateTime startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            ZonedDateTime endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            setMockData(startDay, endDay);

            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween
                    (startDay, endDay)).thenReturn(Collections.emptyList());
            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByHour();
            });
            log.info("NOT_FOUND_DATE.getMessage():{}", NOT_FOUND_DATA.getMessage());
            log.info("exception.getMessage()):{}", exception.getMessage());
            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage());

        }

        @Test
        @DisplayName("시 단위 CPU 사용량 - save fail")
        void failJpaSaveSaveAverageCpuUsageByHourTest() throws Exception {
            ZonedDateTime startDay = parseZonedDateTime("2024-05-23T01:00:00.000+09:00");
            ZonedDateTime endDay = parseZonedDateTime("2024-05-23T02:00:00.000+09:00");
            setMockData(startDay, endDay);
            CpuUsageRateByMinute firstTestData = createCpuUsageRateByMinute(10.0);
            CpuUsageRateByMinute secondTestData = createCpuUsageRateByMinute(20.0);

            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Arrays.asList(firstTestData, secondTestData));

            ArgumentCaptor<CpuUsageRateByHour> captor = ArgumentCaptor.forClass(CpuUsageRateByHour.class);

            when(cpuUsageRateByHourRepository.save(captor.capture()))
                    .thenThrow(new ProcessCustomException(NOT_FOUND_DATA));

            ProcessCustomException exception = assertThrows(ProcessCustomException.class, () -> {
                cpuMonitoringManageService.saveAverageCpuUsageByHour();
            });

            log.info("NOT_FOUND_DATE.getMessage():{}", NOT_FOUND_DATA.getMessage());
            log.info("exception.getMessage()):{}", exception.getMessage());

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage());

        }

    }

//    @Nested
//    @DisplayName("일 단위 CPU 사용량 저장")
//    class SaveAverageCpuUsageByDayTests {
//
//        @Test
//        @DisplayName("일 단위 CPU 사용량 저장 성공")
//        void successSaveAverageCpuUsageByDayTest() throws Exception {
//            String startDayString = "2024-05-23T00:00:00.000+09:00";
//            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
//            String endDayString = "2024-05-24T00:00:00.000+09:00";
//            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
//
//            CpuUsageRateByHour firstTestData = new CpuUsageRateByHour();
//            firstTestData.setMinimumUsage(50.00);
//            firstTestData.setMaximumUsage(50.00);
//            firstTestData.setAverage(50.00);
//            CpuUsageRateByHour secondTestData = new CpuUsageRateByHour();
//            secondTestData.setAverage(25.05);
//
//
//            CpuUsageRateByHour firstTestData = CpuUsageRateByHour.toBuild(50.00, 20.00, 80.00);
//            CpuUsageRateByHour secondTestData = CpuUsageRateByHour.toBuild(30.00, 10.00, 60.00);
//
//            List<CpuUsageRateByHour> stats = Arrays.asList(firstTestData, secondTestData);
//            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(stats);
//
//            ArgumentCaptor<CpuUsageRateByDay> captor = ArgumentCaptor.forClass(CpuUsageRateByDay.class);
//            when(cpuUsageRateByDayRepository.save(captor.capture())).thenReturn(new CpuUsageRateByDay());
//
//            Future<Void> result = cpuMonitoringManageService.saveAverageCpuUsageByDay();
//            result.get();
//            verify(cpuUsageRateByDayRepository).save(any(CpuUsageRateByDay.class));
//        }
//
//        @Test
//        @DisplayName("일 단위 CPU 사용량 저장 실패")
//        void failSaveAverageCpuUsageByDayTest() throws Exception {
//            Timestamp endDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis()));
//            Timestamp startDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - ONE_DAY_BY_MS));
//            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
//            when(dateUtil.getSearchDay(ONE_DAY)).thenReturn(startDay);
//
//            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay)).thenReturn(Arrays.asList());
//
//            assertThrows(CustomException.class, () -> {
//                Future<Void> result = cpuMonitoringManageService.saveAverageCpuUsageByDay();
//                result.get();
//            });
//
//            verify(cpuUsageRateByDayRepository, never()).save(any(CpuUsageRateByDay.class));
//        }
//    }

//    @Nested
//    @DisplayName("분 단위 CPU 사용량 통계 소프트 삭제")
//    class SoftDeleteCpuUsageStatsByMinuteTests {
//
//        @Test
//        @DisplayName("분 단위 CPU 사용량 소프트 삭제 성공")
//        void successSoftDeleteCpuUsageStatsByMinuteTest() throws Exception {
//            Timestamp pastDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - ONE_WEEK));
//            when(dateUtil.getSearchDay(ONE_WEEK)).thenReturn(pastDay);
//
//            List<CpuUsageRateByMinute> oldData = Arrays.asList(new CpuUsageRateByMinute(), new CpuUsageRateByMinute());
//            when(cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);
//
//            Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupCpuUsageStatsByMinute();
//            future.get();
//
//            verify(cpuUsageRateByMinuteRepository).softDeleteOldData(pastDay);
//            verify(cpuUsageRateByMinuteRepository).findByFlag(DELETE_FLAG);
//            verify(cpuMonitoringBackupService).backupCpuUsageStatsByMinute(oldData);
//
//        }
//
//        @Test
//        @DisplayName("분 단위 CPU 사용량 소프트 삭제 실패 - 데이터 X")
//        void failSoftDeleteCpuUsageStatsByMinuteTest() throws Exception {
//            Timestamp pastDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - ONE_WEEK));
//            when(dateUtil.getSearchDay(ONE_WEEK)).thenReturn(pastDay);
//
//            when(cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG)).thenReturn(Arrays.asList());
//
//            assertThrows(CustomException.class, () -> {
//                Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupCpuUsageStatsByMinute();
//                future.get();
//            });
//
//            verify(cpuUsageRateByMinuteRepository).softDeleteOldData(pastDay);
//            verify(cpuUsageRateByMinuteRepository).findByFlag(DELETE_FLAG);
//            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByMinute(any());
//        }
//
//    }
//
//    @Nested
//    @DisplayName("시 단위 CPU 사용량 통계 소프트 삭제")
//    class SoftDeleteCpuUsageStatsByHourTests {
//
//        @Test
//        @DisplayName("시 단위 CPU 사용량 통계 소프트 성공")
//        void successSoftDeleteCpuUsageStatsByHourTest() throws Exception {
//
//
//            when(dateUtil.getTodayTruncatedToDay()).thenReturn(any(ZonedDateTime.class));
//            when(dateUtil.daysAgo(ONE_DAY)).thenReturn(any(ZonedDateTime.class));
//
//            List<CpuUsageRateByHour> oldData = Arrays.asList(new CpuUsageRateByHour(), new CpuUsageRateByHour());
//            when(cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);
//
//            Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByHour();
//            future.get();
//
//            verify(cpuUsageRateByHourRepository).softDeleteOldData(pastDay);
//            verify(cpuUsageRateByHourRepository).findByFlag(DELETE_FLAG);
//            verify(cpuMonitoringBackupService).backupCpuUsageStatsByHour(oldData);
//        }
//
//        @Test
//        @DisplayName("시 단위 CPU 사용량 통계 소프트 실패 - 데이터 X")
//        void failSoftDeleteCpuUsageStatsByHourTest() throws Exception {
//            Timestamp pastDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - THREE_MONTH));
//            when(dateUtil.getSearchMonth(THREE_MONTH)).thenReturn(pastDay);
//
//            when(cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG)).thenReturn(Arrays.asList());
//
//            assertThrows(CustomException.class, () -> {
//                Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByHour();
//                future.get();
//            });
//
//            verify(cpuUsageRateByHourRepository).softDeleteOldData(pastDay);
//            verify(cpuUsageRateByHourRepository).findByFlag(DELETE_FLAG);
//            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByHour(any());
//        }
//
//    }
//
//    @Nested
//    @DisplayName("일 단위 CPU 사용량 통계 소프트 삭제")
//    class SoftDeleteCpuUsageStatsByDayTests {
//
//        @Test
//        @DisplayName("일 단위 CPU 사용량 통계 소프트 성공")
//        void successSoftDeleteCpuUsageStatsByDayTest() throws Exception {
//            Timestamp pastDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - ONE_YEAR));
//            when(dateUtil.getSearchYear(ONE_YEAR)).thenReturn(pastDay);
//
//            List<CpuUsageRateByDay> oldData = Arrays.asList(new CpuUsageRateByDay(), new CpuUsageRateByDay());
//            when(cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG)).thenReturn(oldData);
//
//            Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByDay();
//            future.get();
//
//            verify(cpuUsageRateByDayRepository).softDeleteOldData(pastDay);
//            verify(cpuUsageRateByDayRepository).findByFlag(DELETE_FLAG);
//            verify(cpuMonitoringBackupService).backupCpuUsageStatsByDay(oldData);
//        }
//
//        @Test
//        @DisplayName("일 단위 CPU 사용량 통계 소프트 실패 - 데이터 X")
//        void failSoftDeleteCpuUsageStatsByDayTest() {
//            Timestamp pastDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - ONE_YEAR));
//            when(dateUtil.getSearchYear(ONE_YEAR)).thenReturn(pastDay);
//
//            when(cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG)).thenReturn(Arrays.asList());
//
//            assertThrows(CustomException.class, () -> {
//                Future<Void> future = cpuMonitoringManageService.softDeleteAndBackupOutdatedCpuUsageStatsByDay();
//                future.get();
//            });
//
//            verify(cpuUsageRateByDayRepository).softDeleteOldData(pastDay);
//            verify(cpuUsageRateByDayRepository).findByFlag(DELETE_FLAG);
//            verify(cpuMonitoringBackupService, never()).backupCpuUsageStatsByDay(any());
//        }
//
//    }

}


