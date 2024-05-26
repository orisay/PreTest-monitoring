package com.tera.pretest.cpumonitoring.service;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByDayBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByHourBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByMinuteBackup;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByDayBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByHourBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByMinuteBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.exception.CustomException;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tera.pretest.core.exception.CustomExceptionCode.NOT_FOUND_DATA;
import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.DELETE_FLAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class CpuMonitoringBackupServiceTest {

    @Mock
    protected CpuUsageRateByMinuteBackupRepository cpuUsageRateByMinuteBackupRepository;

    @Mock
    protected CpuUsageRateByHourBackupRepository cpuUsageRateByHourBackupRepository;


    @Mock
    protected CpuUsageRateByDayBackupRepository cpuUsageRateByDayBackupRepository;


    @Mock
    protected CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Mock
    protected CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    @Mock
    protected CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    @Mock
    protected BuildFactory buildFactory;

    @InjectMocks
    protected CpuMonitoringBackupService cpuMonitoringBackupService;

    @Nested
    @DisplayName("분 단위 CPU 사용량 삭제")
    class HardDeleteOutdatedCpuUsageStatsByMinuteTests{

        @Test
        @DisplayName("분 단위 CPU 사용량 삭제 성공")
        void successHardDeleteOutdatedCpuUsageStatsByMinuteTest() throws Exception{
            when(cpuUsageRateByMinuteRepository.deleteByFlag(DELETE_FLAG)).thenReturn(5L);
            cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByMinute();
            verify(cpuUsageRateByMinuteRepository).deleteByFlag(DELETE_FLAG);

        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 삭제")
    class HardDeleteOutdatedCpuUsageStatsByHourTests{
        @Test
        @DisplayName("시 단위 CPU 사용량 삭제 성공")
        void successHardDeleteOutdatedCpuUsageStatsByHourTest()throws Exception{
            when(cpuUsageRateByHourRepository.deleteByFlag(DELETE_FLAG)).thenReturn(5L);
            cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByHour();
            verify(cpuUsageRateByHourRepository).deleteByFlag(DELETE_FLAG);

        }
    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 삭제")
    class HardDeleteOutdatedCpuUsageStatsByDayTests{
        @Test
        @DisplayName("일 단위 CPU 사용량 삭제 성공")
        void successHardDeleteOutdatedCpuUsageStatsByDayTest()throws Exception{
            when(cpuUsageRateByDayRepository.deleteByFlag(DELETE_FLAG)).thenReturn(5L);
            cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByDay();
            verify(cpuUsageRateByDayRepository).deleteByFlag(DELETE_FLAG);
        }
    }

    @Nested
    @DisplayName("분 단위 CPU 사용량 백업")
    class BackupCpuUsageStatsByMinuteTests {

        @Test
        @DisplayName("분 단위 CPU 사용량 백업")
        void successBackupCpuUsageStatsByMinuteTest() throws Exception{
            List<CpuUsageRateByMinute> oldData = Arrays.asList(new CpuUsageRateByMinute());
            List<CpuUsageRateByMinuteBackup> backupData = Arrays.asList(new CpuUsageRateByMinuteBackup());

            when(buildFactory.toBackupDataByMinuteStats(oldData)).thenReturn(backupData);
            cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);

            verify(cpuUsageRateByMinuteBackupRepository).saveAll(backupData);
        }


        @Test
        @DisplayName("분 단위 CPU 사용량 백업 - 예외 처리 체크")
        void failBackupCpuUsageStatsByMinuteTest() throws Exception{
            List<CpuUsageRateByMinute> oldData = Arrays.asList(new CpuUsageRateByMinute());
            List<CpuUsageRateByMinuteBackup> emptyBackupData = Collections.emptyList();

            when(buildFactory.toBackupDataByMinuteStats(oldData)).thenReturn(emptyBackupData);

            CustomException exception = assertThrows(CustomException.class, () -> {
                cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);
            });

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외 경우가 일치하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 백업")
    class BackupCpuUsageStatsByHourTests {

        @Test
        @DisplayName("시 단위 CPU 사용량 백업")
        void successBackupCpuUsageStatsByHourTest() throws Exception{
            List<CpuUsageRateByHour> oldData = Arrays.asList(new CpuUsageRateByHour());
            List<CpuUsageRateByHourBackup> backupData = Arrays.asList(new CpuUsageRateByHourBackup());

            when(buildFactory.toBackupDataByHourStats(oldData)).thenReturn(backupData);
            cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);

            verify(cpuUsageRateByHourBackupRepository).saveAll(backupData);
        }


        @Test
        @DisplayName("시 단위 CPU 사용량 백업 - 예외 처리 체크")
        void failBackupCpuUsageStatsByHourTest() throws Exception{
            List<CpuUsageRateByHour> oldData = Arrays.asList(new CpuUsageRateByHour());
            List<CpuUsageRateByHourBackup> emptyBackupData = Collections.emptyList();

            when(buildFactory.toBackupDataByHourStats(oldData)).thenReturn(emptyBackupData);

            CustomException exception = assertThrows(CustomException.class, () -> {
                cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);
            });

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외 경우가 일치하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 백업")
    class BackupCpuUsageStatsByDayTests {

        @Test
        @DisplayName("일 단위 CPU 사용량 백업")
        void successBackupCpuUsageStatsByDayTest() throws Exception{
            List<CpuUsageRateByDay> oldData = Arrays.asList(new CpuUsageRateByDay());
            List<CpuUsageRateByDayBackup> backupData = Arrays.asList(new CpuUsageRateByDayBackup());

            when(buildFactory.toBackupDataByDayStats(oldData)).thenReturn(backupData);
            cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);

            verify(cpuUsageRateByDayBackupRepository).saveAll(backupData);
        }


        @Test
        @DisplayName("일 단위 CPU 사용량 백업 - 예외 처리 체크")
        void failBackupCpuUsageStatsByDayTest() throws Exception{
            List<CpuUsageRateByDay> oldData = Arrays.asList(new CpuUsageRateByDay());
            List<CpuUsageRateByDayBackup> emptyBackupData = Collections.emptyList();

            when(buildFactory.toBackupDataByDayStats(oldData)).thenReturn(emptyBackupData);

            CustomException exception = assertThrows(CustomException.class, () -> {
                cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);
            });

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), "예외 경우가 일치하지 않습니다.");

        }
    }
}
