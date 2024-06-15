package com.tera.pretest.cpumonitoring.service.unit;

import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import com.tera.pretest.core.exception.restful.CustomException;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.util.ProviderDateUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.tera.pretest.core.contant.MonitoringConstant.ONE_DAY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertFalse;

/*Unit Test CpuMonitoringServiceTest
 *Line coverage 100%
 *Class coverage 100%
 *Method coverage 100%
 * */
@Log4j2
@DisplayName("CpuMonitoringService Tests")
@ExtendWith(MockitoExtension.class)
@Import({ZonedDateTimeFormatConfig.class, CpuMonitoringService.class})
public class CpuMonitoringServiceTest {
    @Mock
    private ProviderDateUtil dateUtil;

    @Mock
    private CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Mock
    private CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    @Mock
    private CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    @Mock
    private BuildFactory buildFactory;

    @InjectMocks
    private CpuMonitoringService cpuMonitoringService;

    @Mock
    private ShutdownManager shutdownManager;

    private DateTimeFormatter formatter;


    @BeforeEach
    public void setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    @AfterEach
    public void shutUp() {
        shutdownManager.shutdown();
    }

    @Nested
    @DisplayName("분 단위 CPU 사용률 조회")
    class GetCpuUsageRateByMinuteTests {


        @Test
        @DisplayName("분 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByMinuteTest() throws Exception {
            GetCpuUsageRateByMinute mockRequest = Mockito.mock(GetCpuUsageRateByMinute.class);
            String startTimeString = "2024-05-23T01:00:00.000+09:00";
            ZonedDateTime startTime = ZonedDateTime.parse(startTimeString, formatter);
            String endTimeString = "2024-05-23T02:00:00.000+09:00";
            ZonedDateTime endTime = ZonedDateTime.parse(endTimeString, formatter);
            mockRequest.setStartTime(startTime);

            when(mockRequest.getStartTime()).thenReturn(startTime);
            when(dateUtil.truncateZonedDateTimeToHour(eq(startTime))).thenReturn(startTime);
            when(dateUtil.addOneHour(eq(startTime))).thenReturn(endTime);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByMinute()));

            ResultCpuUsageRateByMinute result = cpuMonitoringService.getCpuUsageRateByMinute(mockRequest);

            verify(mockRequest).getStartTime();
            verify(dateUtil).truncateZonedDateTimeToHour(any(ZonedDateTime.class));
            verify(dateUtil).addOneHour(eq(startTime));
            verify(cpuUsageRateByMinuteRepository).findByCreateTimeBetween(startTime, endTime);

            assertFalse("빈 객체 반환.", result.getStatsUsage().isEmpty());
        }

        @Test
        @DisplayName("분 단위 CPU 사용률 - 조건문 테스트")
        void failGetCpuUsageRateByMinuteTest() throws Exception {
            GetCpuUsageRateByMinute mockRequest = Mockito.mock(GetCpuUsageRateByMinute.class);
            String startTimeString = "2024-05-23T00:00:00.000+09:00";
            ZonedDateTime startTime = ZonedDateTime.parse(startTimeString, formatter);
            String endTimeString = "2024-05-23T01:00:00.000+09:00";
            ZonedDateTime endTime = ZonedDateTime.parse(endTimeString, formatter);
            mockRequest.setStartTime(startTime);

            when(mockRequest.getStartTime()).thenReturn(startTime);
            when(dateUtil.truncateZonedDateTimeToHour(eq(startTime))).thenReturn(startTime);
            when(dateUtil.addOneHour(any())).thenReturn(endTime);
            when(cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                log.info("failGetCpuUsageRateByMinuteTest CustomException Test");
                cpuMonitoringService.getCpuUsageRateByMinute(mockRequest);
            });

            verify(mockRequest).getStartTime();
            verify(dateUtil).truncateZonedDateTimeToHour(any(ZonedDateTime.class));
            verify(dateUtil).addOneHour(eq(startTime));
            verify(cpuUsageRateByMinuteRepository).findByCreateTimeBetween(startTime, endTime);
        }
    }

    @Nested
    @DisplayName("시 단위 CPU 사용률 조회")
    class GetCpuUsageRateByHourTests {

        @Test
        @DisplayName("시 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByHourTest() throws Exception {
            GetCpuUsageRateByHour mockRequest = Mockito.mock(GetCpuUsageRateByHour.class);
            String startDayString = "2024-05-23T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
            mockRequest.setStartDay(startDay);


            when(mockRequest.getStartDay()).thenReturn(startDay);
            when(dateUtil.truncateZonedDateTimeToDay(eq(startDay))).thenReturn(startDay);
            when(dateUtil.addOneDay(eq(startDay))).thenReturn(endDay);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByHour()));

            ResultCpuUsageRateByHour result = cpuMonitoringService.getCpuUsageRateByHour(mockRequest);

            verify(mockRequest).getStartDay();
            verify(dateUtil).truncateZonedDateTimeToDay(any(ZonedDateTime.class));
            verify(dateUtil).addOneDay(eq(startDay));
            verify(cpuUsageRateByHourRepository).findByCreateTimeBetween(startDay, endDay);
            assertFalse("빈 객체 반환", result.getStatsUsage().isEmpty());

        }


        @Test
        @DisplayName("시 단위 CPU 사용률 조회 - 조건문")
        void failGetCpuUsageRateByHourTest() throws Exception {
            GetCpuUsageRateByHour mockRequest = Mockito.mock(GetCpuUsageRateByHour.class);
            String startDayString = "2024-05-23T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
            mockRequest.setStartDay(startDay);

            when(mockRequest.getStartDay()).thenReturn(startDay);
            when(dateUtil.truncateZonedDateTimeToDay(eq(startDay))).thenReturn(startDay);
            when(dateUtil.addOneDay(eq(startDay))).thenReturn(endDay);
            when(cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByHour(mockRequest);
            });

            verify(mockRequest).getStartDay();
            verify(dateUtil).truncateZonedDateTimeToDay(any(ZonedDateTime.class));
            verify(dateUtil).addOneDay(eq(startDay));
            verify(cpuUsageRateByHourRepository).findByCreateTimeBetween(startDay, endDay);


        }

    }

    @Nested
    @DisplayName("일 단위 CPU 사용률 조회")
    class GetCpuUsageRateByDayTests {

        @Test
        @DisplayName("일 단위 CPU 사용률 조회 성공")
        void successGetCpuUsageRateByDayTest() throws Exception {
            GetCpuUsageRateByDay mockRequest = Mockito.mock(GetCpuUsageRateByDay.class);
            String startDayString = "2024-05-20T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
            mockRequest.setStartDay(startDay);
            mockRequest.setEndDay(endDay);

            when(mockRequest.getStartDay()).thenReturn(startDay);
            when(mockRequest.getEndDay()).thenReturn(endDay);
            when(dateUtil.truncateZonedDateTimeToDay(mockRequest.getStartDay())).thenReturn(startDay);
            when(dateUtil.truncateZonedDateTimeToDay(mockRequest.getEndDay())).thenReturn(endDay);
            when(dateUtil.isSameDay(endDay)).thenReturn(true);

            ZonedDateTime exactEndDay = endDay.plusDays(ONE_DAY);
            when(dateUtil.addOneDay(endDay)).thenReturn(exactEndDay);

            log.debug("Test parameter startDay:{} exactEndDay:{}", startDay, exactEndDay);

            when(cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, exactEndDay))
                    .thenReturn(Collections.singletonList(new CpuUsageRateByDay()));

            ResultCpuUsageRateByDay result = cpuMonitoringService.getCpuUsageRateByDay(mockRequest);

            assertFalse("빈 객체 반환", result.getStatsUsage().isEmpty());

            verify(dateUtil).truncateZonedDateTimeToDay(mockRequest.getStartDay());
            verify(dateUtil).truncateZonedDateTimeToDay(mockRequest.getEndDay());
            verify(dateUtil).isSameDay(endDay);
            verify(dateUtil).addOneDay(endDay);
            verify(cpuUsageRateByDayRepository).findByCreateTimeBetween(startDay,exactEndDay);

        }

        @Test
        @DisplayName("일 단위 CPU 사용률 조회 실패")
        void failGetCpuUsageRateByDayTest() throws Exception {
            GetCpuUsageRateByDay mockRequest = Mockito.mock(GetCpuUsageRateByDay.class);
            String startDayString = "2024-05-20T00:00:00.000+09:00";
            ZonedDateTime startDay = ZonedDateTime.parse(startDayString, formatter);
            String endDayString = "2024-05-24T00:00:00.000+09:00";
            ZonedDateTime endDay = ZonedDateTime.parse(endDayString, formatter);
            mockRequest.setStartDay(startDay);
            mockRequest.setEndDay(endDay);

            when(mockRequest.getStartDay()).thenReturn(startDay);
            when(mockRequest.getEndDay()).thenReturn(endDay);
            when(dateUtil.truncateZonedDateTimeToDay(mockRequest.getStartDay())).thenReturn(startDay);
            when(dateUtil.truncateZonedDateTimeToDay(mockRequest.getEndDay())).thenReturn(endDay);
            when(dateUtil.isSameDay(endDay)).thenReturn(false);

            ZonedDateTime exactEndDay = endDay;

            when(cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, endDay))
                    .thenReturn(Collections.emptyList());
            assertThrows(CustomException.class, () -> {
                cpuMonitoringService.getCpuUsageRateByDay(new GetCpuUsageRateByDay(startDay, exactEndDay));
            });

            verify(dateUtil).truncateZonedDateTimeToDay(mockRequest.getStartDay());
            verify(dateUtil).truncateZonedDateTimeToDay(mockRequest.getEndDay());
            verify(dateUtil).isSameDay(endDay);
            verify(cpuUsageRateByDayRepository).findByCreateTimeBetween(startDay,exactEndDay);


        }


    }
}
