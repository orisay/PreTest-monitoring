//package com.tera.pretest.cpumonitoring.service;
//
//import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
//import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
//import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
//import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
//import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
//import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
//import com.tera.pretest.core.exception.restful.CustomException;
//import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
//import com.tera.pretest.core.monitoring.service.CpuMonitoringManageService;
//import com.tera.pretest.core.util.DateUtil;
//import com.tera.pretest.cpumonitoring.util.TestUtil;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import oshi.hardware.CentralProcessor;
//
//import java.sql.Timestamp;
//import java.time.ZonedDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.Future;
//
//import static com.tera.pretest.core.contant.MonitoringConstant.*;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.util.AssertionErrors.assertEquals;
//
//@Log4j2
//@ExtendWith(MockitoExtension.class)
//public class CpuMonitoringManageServiceTest {
//
//    @Mock
//    protected CentralProcessor centralProcessor;
//
//    @Mock
//    protected CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;
//
//    @Mock
//    protected CpuUsageRateByHourRepository cpuUsageRateByHourRepository;
//
//    @Mock
//    protected CpuUsageRateByDayRepository cpuUsageRateByDayRepository;
//
//    @Mock
//    protected CpuMonitoringBackupService cpuMonitoringBackupService;
//
//    @Mock
//    protected DateUtil dateUtil;
//
//    @InjectMocks
//    protected CpuMonitoringManageService cpuMonitoringManageService;
//
//    @Nested
//    @DisplayName("분 단위 CPU 사용량 저장")
//    class SaveMonitoringCpuUsageTests {
//
//        @Test
//        @DisplayName("분 단위 CPU 사용량 저장 성공")
//        void successSaveMonitoringCpuUsageTest() throws Exception {
//            Double secondTick = 0.12; //예상 값 0.00 ~ 1.00
//            when(centralProcessor.getSystemCpuLoadTicks()).thenReturn(new long[]{1, 2, 3, 4});
//            when(centralProcessor.getSystemCpuLoadBetweenTicks(any(long[].class))).thenReturn(secondTick);
//
//            ArgumentCaptor<CpuUsageRateByMinute> captor = ArgumentCaptor.forClass(CpuUsageRateByMinute.class);
//            when(cpuUsageRateByMinuteRepository.save(captor.capture())).thenReturn(new CpuUsageRateByMinute());
//
//            Future<Void> future = cpuMonitoringManageService.saveMonitoringCpuUsage();
//            future.get();
//
//            Double averageCpuUsageFormatted = TestUtil.changeDecimalFormatCpuUsage(secondTick * PERCENTAGE);
//            verify(cpuUsageRateByMinuteRepository).save(any(CpuUsageRateByMinute.class));
//            assertEquals("저장 된 CPU 사용률 예상 값과 다릅니다.", captor.getValue().getUsageRate(), averageCpuUsageFormatted);
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
//
//    @Nested
//    @DisplayName("시 단위 CPU 사용량 저장")
//    class SaveAverageCpuUsageByHourTests {
//
//        @Test
//        @DisplayName("시 단위 CPU 사용량 저장 성공")
//        void successSaveAverageCpuUsageByHourTest() throws Exception {
//            String startDayString = "2024-05-23T00:00:00.000+09:00";
//            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
//            String endDayString = "2024-05-24T00:00:00.000+09:00";
//            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
//
//            CpuUsageRateByMinute firstTestData = new CpuUsageRateByMinute();
//            firstTestData.setUsageRate(50.00);
//            CpuUsageRateByMinute secondTestData = new CpuUsageRateByMinute();
//            secondTestData.setUsageRate(25.05);
//
//            List<CpuUsageRateByMinute> stats = Arrays.asList(firstTestData, secondTestData);
//            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime)).thenReturn(stats);
//            ArgumentCaptor<CpuUsageRateByHour> captor = ArgumentCaptor.forClass(CpuUsageRateByHour.class);
//            when(cpuUsageRateByHourRepository.save(captor.capture())).thenReturn(new CpuUsageRateByHour());
//
//            Future<Void> result = cpuMonitoringManageService.saveAverageCpuUsageByHour();
//            result.get();
//            verify(cpuUsageRateByHourRepository).save(any(CpuUsageRateByHour.class));
//        }
//
//
//        @Test
//        @DisplayName("시 단위 CPU 사용량 저장 실패")
//        void failSaveAverageCpuUsageByHourTest() throws Exception {
//            Timestamp endTime = dateUtil.truncateTimestampToHour(new Timestamp(System.currentTimeMillis()));
//            Timestamp startTime = dateUtil.truncateTimestampToHour(new Timestamp(System.currentTimeMillis() - ONE_HOUR_BY_MS));
//            when(dateUtil.getTodayTruncatedToHour()).thenReturn(endTime);
//            when(dateUtil.getSearchHour(ONE_HOUR)).thenReturn(startTime);
//
//            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime)).thenReturn(Arrays.asList());
//
//            assertThrows(CustomException.class, () -> {
//                Future<Void> result = cpuMonitoringManageService.saveAverageCpuUsageByHour();
//                result.get();
//            });
//
//            verify(cpuUsageRateByHourRepository, never()).save(any(CpuUsageRateByHour.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("일 단위 CPU 사용량 저장")
//    class SaveAverageCpuUsageByDayTests {
//
//        @Test
//        @DisplayName("일 단위 CPU 사용량 저장 성공")
//        void successSaveAverageCpuUsageByDayTest() throws Exception {
//            Timestamp endDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis()));
//            Timestamp startDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - ONE_DAY_BY_MS));
//            when(dateUtil.getTodayTruncatedToDay()).thenReturn(endDay);
//            when(dateUtil.getSearchDay(ONE_DAY)).thenReturn(startDay);
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
//
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
//            Timestamp pastDay = dateUtil.truncateTimestampToDay(new Timestamp(System.currentTimeMillis() - THREE_MONTH));
//            when(dateUtil.getSearchMonth(THREE_MONTH)).thenReturn(pastDay);
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
//
//}
