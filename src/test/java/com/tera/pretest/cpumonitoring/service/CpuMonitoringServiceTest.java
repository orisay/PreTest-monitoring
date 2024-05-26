package com.tera.pretest.cpumonitoring.service;

import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import com.tera.pretest.core.exception.CustomException;
import com.tera.pretest.core.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class CpuMonitoringServiceTest {
    @Mock
    protected DateUtil dateUtil;

    @Mock
    protected CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Mock
    protected CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    @Mock
    protected CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    @InjectMocks
    protected CpuMonitoringService cpuMonitoringService;

    @Nested
    @DisplayName("분 단위 CPU 사용률 조회")
    class GetCpuUsageRateByMinuteTests {

        @Test
        @DisplayName("분 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByMinuteTest() throws Exception {
            Timestamp startDay = Timestamp.valueOf("2024-05-23 00:00:00");
            Timestamp endDay = Timestamp.valueOf("2024-05-23 01:00:00");

            when(dateUtil.truncateTimestampToHour(any())).thenReturn(startDay);
            when(dateUtil.addOneHour(any())).thenReturn(endDay);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByMinute()));

            ResultCpuUsageRateByMinute result = cpuMonitoringService.getCpuUsageRateByMinute(new GetCpuUsageRateByMinute(startDay));
            assertFalse("빈 객체 반환.", result.getStatsUsage().isEmpty());
        }

        @Test
        @DisplayName("분 단위 CPU 사용률 실패 케이스")
        void failGetCpuUsageRateByMinuteTest() throws Exception {
            Timestamp startDay = Timestamp.valueOf("2024-05-23 00:00:00");
            Timestamp endDay = Timestamp.valueOf("2024-05-25 01:00:00");

            when(dateUtil.truncateTimestampToHour(any())).thenReturn(startDay);
            when(dateUtil.addOneHour(any())).thenReturn(endDay);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByMinute(new GetCpuUsageRateByMinute(startDay));
            });
        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용률 조회")
    class GetCpuUsageRateByHourTests {

        @Test
        @DisplayName("시 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByHourTest() throws Exception {
            Timestamp startDay = Timestamp.valueOf("2024-05-20 00:00:00");
            Timestamp endDay = Timestamp.valueOf("2024-05-21 00:00:00");
            when(dateUtil.truncateTimestampToDay(any())).thenReturn(startDay);
            when(dateUtil.addOneDayByInputDay(any())).thenReturn(endDay);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByHour()));
            ResultCpuUsageRateByHour result = cpuMonitoringService.getCpuUsageRateByHour(new GetCpuUsageRateByHour(startDay));
            assertFalse("빈 객체 반환", result.getStatsUsage().isEmpty());

        }

        @Test
        @DisplayName("시 단위 CPU 사용률 조회 실패 케이스")
        void failGetCpuUsageRateByHourTest() throws Exception {
            Timestamp startDay = Timestamp.valueOf("2024-05-20 00:00:00");
            Timestamp endDay = Timestamp.valueOf("2024-05-21 00:00:00");

            when(dateUtil.truncateTimestampToDay(any())).thenReturn(startDay);
            when(dateUtil.addOneDayByInputDay(any())).thenReturn(endDay);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByHour(new GetCpuUsageRateByHour(startDay));
            });

        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용률 조회")
    class GetCpuUsageRateByDayTests {

        @Test
        @DisplayName("일 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByDayTest() throws Exception {
            Timestamp startDay = Timestamp.valueOf("2024-05-20 00:00:00");
            Timestamp endDay = Timestamp.valueOf("2024-05-26 00:00:00");

            when(dateUtil.truncateTimestampToDay(any())).thenReturn(startDay, endDay);
            when(cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByDay()));
            ResultCpuUsageRateByDay result = cpuMonitoringService.getCpuUsageRateByDay(new GetCpuUsageRateByDay(startDay, endDay));
            assertFalse("빈 객체 반환", result.getStatsUsage().isEmpty());
        }

        @Test
        @DisplayName("일 단위 CPU 사용률 조회 실패")
        void failGetCpuUsageRateByDayTest() throws Exception {
            Timestamp startDay = Timestamp.valueOf("2024-05-20 00:00:00");
            Timestamp endDay = Timestamp.valueOf("2024-05-26 00:00:00");

            when(dateUtil.truncateTimestampToDay(any())).thenReturn(startDay, endDay);
            when(cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByDay(new GetCpuUsageRateByDay(startDay, endDay));
            });

        }


    }
}
