package com.tera.pretest.cpumonitoring.service;

import com.tera.pretest.config.UnitTestConfig;
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
import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import com.tera.pretest.core.exception.restful.CustomException;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.util.DateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@Log4j2
@ExtendWith(MockitoExtension.class)
@Import(ZonedDateTimeFormatConfig.class)
@SpringBootTest(classes = {UnitTestConfig.class})
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

    @Autowired
    protected   DateTimeFormatter formatter = ZonedDateTimeFormatConfig.dateTimeFormatter;


    @AfterAll
    static void shutUp() {
        TimeProvider.getInstance().shutdown();
    }

    @Nested
    @DisplayName("분 단위 CPU 사용률 조회")
    class GetCpuUsageRateByMinuteTests {
//
//
//        @Test
//        @DisplayName("분 단위 CPU 사용률 조회 성공")
//        void successGetCpuUsageRateByMinuteTest() throws Exception {
//            DateTimeFormatter formatter = ZonedDateTimeFormatConfig.dateTimeFormatter;
//            String startDayString = "2024-05-23T00:00:00+09:00";
//            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
//            String endDayString = "2024-05-23T01:00:00+09:00";
//            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
//
//            when(dateUtil.truncateZonedDateTimeToDay(any())).thenReturn(startDay);
//            when(dateUtil.addOneHour(any())).thenReturn(endDay);
//            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay))
//                    .thenReturn(Collections.singletonList(new CpuUsageRateByMinute()));
//
//            ResultCpuUsageRateByMinute result = cpuMonitoringService.getCpuUsageRateByMinute(new GetCpuUsageRateByMinute(startDay));
//            assertFalse("빈 객체 반환.", result.getStatsUsage().isEmpty());
//        }
//
//        @Test
//        @DisplayName("분 단위 CPU 사용률 실패 케이스")
//        void failGetCpuUsageRateByMinuteTest() throws Exception {
//            String startDayString = "2024-05-23T00:00:00+09:00";
//            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
//            String endDayString = "2024-05-23T01:00:00+09:00";
//            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
//
//            when(dateUtil.truncateZonedDateTimeToHour(any())).thenReturn(startDay);
//            when(dateUtil.addOneHour(any())).thenReturn(endDay);
//            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay))
//                    .thenReturn(Collections.emptyList());
//            assertThrows(CustomException.class, () -> {
//                cpuMonitoringService.getCpuUsageRateByMinute(new GetCpuUsageRateByMinute(startDay));
//            });
//        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용률 조회")
    class GetCpuUsageRateByHourTests {

        @Test
        @DisplayName("시 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByHourTest() throws Exception {
            String startDayString = "2024-05-23T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);

            when(dateUtil.truncateZonedDateTimeToDay(any())).thenReturn(startDay);
            when(dateUtil.addOneDayByInputDay(any())).thenReturn(endDay);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByHour()));
            ResultCpuUsageRateByHour result = cpuMonitoringService.getCpuUsageRateByHour(new GetCpuUsageRateByHour(startDay));
            assertFalse("빈 객체 반환", result.getStatsUsage().isEmpty());

        }

        @Test
        @DisplayName("시 단위 CPU 사용률 조회 실패 케이스")
        void failGetCpuUsageRateByHourTest() throws Exception {
            String startDayString = "2024-05-23T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);

            when(dateUtil.truncateZonedDateTimeToDay(any())).thenReturn(startDay);
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
            String startDayString = "2024-05-20T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);

            when(dateUtil.truncateZonedDateTimeToDay(any())).thenReturn(startDay, endDay);
            when(cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByDay()));
            ResultCpuUsageRateByDay result = cpuMonitoringService.getCpuUsageRateByDay(new GetCpuUsageRateByDay(startDay, endDay));
            assertFalse("빈 객체 반환", result.getStatsUsage().isEmpty());
        }

        @Test
        @DisplayName("일 단위 CPU 사용률 조회 실패")
        void failGetCpuUsageRateByDayTest() throws Exception {
            String startDayString = "2024-05-20T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);

            when(dateUtil.truncateZonedDateTimeToDay(any())).thenReturn(startDay, endDay);
            when(cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByDay(new GetCpuUsageRateByDay(startDay, endDay));
            });

        }


    }
}
