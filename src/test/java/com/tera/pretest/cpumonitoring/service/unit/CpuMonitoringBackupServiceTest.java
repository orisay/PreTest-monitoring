package com.tera.pretest.cpumonitoring.service.unit;

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
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.tera.pretest.core.constant.MonitoringConstant.DELETE_FLAG;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;
import static com.tera.pretest.cpumonitoring.core.constant.TestConstant.NOT_MATCH_EXCEPTION_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*Unit Test DefaultCpuMonitoringManageServiceTest
 *Line coverage 100%
 *Class coverage 100%
 *Method coverage 100%
 * */
@Log4j2
@DisplayName("CpuMonitoringBackupService Tests")
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

    @BeforeEach
    public void setup() {
    }

    @Nested
    @DisplayName("분 단위 CPU 사용량 삭제")
    class HardDeleteOutdatedCpuUsageStatsByMinuteTests {

        @Test
        @DisplayName("분 단위 CPU 사용량 삭제 성공")
        void successHardDeleteOutdatedCpuUsageStatsByMinuteTest() {
            when(cpuUsageRateByMinuteRepository.deleteByFlag(DELETE_FLAG)).thenReturn(5L);
            cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByMinute();
            verify(cpuUsageRateByMinuteRepository).deleteByFlag(DELETE_FLAG);

        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 삭제")
    class HardDeleteOutdatedCpuUsageStatsByHourTests {
        @Test
        @DisplayName("시 단위 CPU 사용량 삭제 성공")
        void successHardDeleteOutdatedCpuUsageStatsByHourTest() {
            when(cpuUsageRateByHourRepository.deleteByFlag(DELETE_FLAG)).thenReturn(5L);
            cpuMonitoringBackupService.hardDeleteOutdatedCpuUsageStatsByHour();
            verify(cpuUsageRateByHourRepository).deleteByFlag(DELETE_FLAG);

        }
    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 삭제")
    class HardDeleteOutdatedCpuUsageStatsByDayTests {
        @Test
        @DisplayName("일 단위 CPU 사용량 삭제 성공")
        void successHardDeleteOutdatedCpuUsageStatsByDayTest() {
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
        void successBackupCpuUsageStatsByMinuteTest() {
            List<CpuUsageRateByMinute> oldData = List.of(new CpuUsageRateByMinute());
            List<CpuUsageRateByMinuteBackup> backupData = List.of(new CpuUsageRateByMinuteBackup());

            when(buildFactory.toBackupDataByMinuteStats(oldData)).thenReturn(backupData);
            cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);

            verify(cpuUsageRateByMinuteBackupRepository).saveAll(backupData);
        }


        @Test
        @DisplayName("분 단위 CPU 사용량 백업 - 예외 처리 체크")
        void failBackupCpuUsageStatsByMinuteTest() {
            List<CpuUsageRateByMinute> oldData = List.of(new CpuUsageRateByMinute());
            List<CpuUsageRateByMinuteBackup> emptyBackupData = Collections.emptyList();

            when(buildFactory.toBackupDataByMinuteStats(oldData)).thenReturn(emptyBackupData);

            ProcessCustomException exception = assertThrows(ProcessCustomException.class,
                    () -> cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData));


            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);
        }

    }

    @Nested
    @DisplayName("시 단위 CPU 사용량 백업")
    class BackupCpuUsageStatsByHourTests {

        @Test
        @DisplayName("시 단위 CPU 사용량 백업")
        void successBackupCpuUsageStatsByHourTest() {
            List<CpuUsageRateByHour> oldData = List.of(new CpuUsageRateByHour());
            List<CpuUsageRateByHourBackup> backupData = List.of(new CpuUsageRateByHourBackup());

            when(buildFactory.toBackupDataByHourStats(oldData)).thenReturn(backupData);
            cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);

            verify(cpuUsageRateByHourBackupRepository).saveAll(backupData);
        }


        @Test
        @DisplayName("시 단위 CPU 사용량 백업 - 예외 처리 체크")
        void failBackupCpuUsageStatsByHourTest() {
            List<CpuUsageRateByHour> oldData = List.of(new CpuUsageRateByHour());
            List<CpuUsageRateByHourBackup> emptyBackupData = Collections.emptyList();

            when(buildFactory.toBackupDataByHourStats(oldData)).thenReturn(emptyBackupData);

            ProcessCustomException exception = assertThrows(ProcessCustomException.class,
                    () -> cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData));

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용량 백업")
    class BackupCpuUsageStatsByDayTests {

        @Test
        @DisplayName("일 단위 CPU 사용량 백업")
        void successBackupCpuUsageStatsByDayTest() {
            List<CpuUsageRateByDay> oldData = List.of(new CpuUsageRateByDay());
            List<CpuUsageRateByDayBackup> backupData = List.of(new CpuUsageRateByDayBackup());
            when(buildFactory.toBackupDataByDayStats(eq(oldData))).thenReturn(backupData);
            cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);

            verify(cpuUsageRateByDayBackupRepository).saveAll(backupData);

        }


        @Test
        @DisplayName("일 단위 CPU 사용량 백업 - 예외 처리 체크")
        void failBackupCpuUsageStatsByDayTest() throws ProcessCustomException {
            List<CpuUsageRateByDay> oldData = List.of(new CpuUsageRateByDay());
            List<CpuUsageRateByDayBackup> emptyBackupData = Collections.emptyList();

            when(buildFactory.toBackupDataByDayStats(oldData)).thenReturn(emptyBackupData);

            ProcessCustomException exception = assertThrows(ProcessCustomException.class,
                    () -> cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData));

            assertEquals(NOT_FOUND_DATA.getMessage(), exception.getMessage(), NOT_MATCH_EXCEPTION_MESSAGE);

        }
    }
}
