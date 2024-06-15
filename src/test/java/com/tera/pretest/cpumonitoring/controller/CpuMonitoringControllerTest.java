package com.tera.pretest.cpumonitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tera.pretest.context.cpumonitoring.controller.CpuMonitoringController;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import com.tera.pretest.core.exception.restful.CustomException;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.util.ProviderDateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.Collections;

import static com.tera.pretest.core.exception.restful.CustomExceptionCode.NOT_FOUND_DATA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CpuMonitoringController.class)
public class CpuMonitoringControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected CpuMonitoringService cpuMonitoringService;

    @MockBean
    protected ProviderDateUtil dateUtil;

    @MockBean
    protected ShutdownManager shutdownManager;

    @AfterEach
    public void shutUp(){
        shutdownManager.shutdown();
    }

    @Test
    @DisplayName("분 단위 CPU 사용률 조회 - 성공")
    void successGetCpuUsageRateByMinute() throws Exception {
        ZonedDateTime insertDay = ZonedDateTime.now();
        GetCpuUsageRateByMinute requestDto = new GetCpuUsageRateByMinute();
        requestDto.setStartTime(dateUtil.truncateZonedDateTimeToDay(insertDay));

        CpuUsageRateByMinute usageRate = new CpuUsageRateByMinute(1L, 12.34);
        ResultCpuUsageRateByMinute responseDto = new ResultCpuUsageRateByMinute(Collections.singletonList(usageRate));

        when(cpuMonitoringService.getCpuUsageRateByMinute(any(GetCpuUsageRateByMinute.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring/get/minute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statsUsage[0].usageRate").value(12.34));
    }

    @Test
    @DisplayName("분 단위 CPU 사용률 조회 - 데이터베이스 값 없음")
    void failGetCpuUsageRateByMinute_NotFound() throws Exception {
        GetCpuUsageRateByMinute requestDto = new GetCpuUsageRateByMinute();
        ZonedDateTime insertDay = ZonedDateTime.now();
        requestDto.setStartTime(insertDay);

        when(cpuMonitoringService.getCpuUsageRateByMinute(any(GetCpuUsageRateByMinute.class)))
                .thenThrow(new CustomException(NOT_FOUND_DATA));

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring/get/minute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND_DATA"))
                .andExpect(jsonPath("$.message").value(NOT_FOUND_DATA.getMessage()));
    }

    @Test
    @DisplayName("시 단위 CPU 사용률 조회 - 성공")
    void successGetCpuUsageRateByHour() throws Exception {
        GetCpuUsageRateByHour requestDto = new GetCpuUsageRateByHour();
        ZonedDateTime insertDay = ZonedDateTime.now();
        requestDto.setStartDay(insertDay);

        CpuUsageRateByHour usageRate = new CpuUsageRateByHour(1L, 20.45, 30.67, 10.23);
        ResultCpuUsageRateByHour responseDto = new ResultCpuUsageRateByHour(Collections.singletonList(usageRate));

        when(cpuMonitoringService.getCpuUsageRateByHour(any(GetCpuUsageRateByHour.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring/get/hour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statsUsage[0].average").value(20.45))
                .andExpect(jsonPath("$.statsUsage[0].maximumUsage").value(30.67))
                .andExpect(jsonPath("$.statsUsage[0].minimumUsage").value(10.23));
    }

    @Test
    @DisplayName("시 단위 CPU 사용률 조회 - 데이터베이스 값 없음")
    void failGetCpuUsageRateByHour_NotFound() throws Exception {
        GetCpuUsageRateByHour requestDto = new GetCpuUsageRateByHour();
        ZonedDateTime insertDay = ZonedDateTime.now();
        requestDto.setStartDay(insertDay);

        when(cpuMonitoringService.getCpuUsageRateByHour(any(GetCpuUsageRateByHour.class)))
                .thenThrow(new CustomException(NOT_FOUND_DATA));

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring/get/hour")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND_DATA"))
                .andExpect(jsonPath("$.message").value(NOT_FOUND_DATA.getMessage()));
    }

    @Test
    @DisplayName("일 단위 CPU 사용률 조회 - 성공")
    void successGetCpuUsageRateByDay() throws Exception {
        GetCpuUsageRateByDay requestDto = new GetCpuUsageRateByDay();
        ZonedDateTime insertDay = ZonedDateTime.now();
        ZonedDateTime insertDay2 = dateUtil.getSearchDay(10);
        requestDto.setStartDay(insertDay);
        requestDto.setEndDay(insertDay2);

        CpuUsageRateByDay usageRate = new CpuUsageRateByDay(1L, 25.67, 40.89, 15.34);
        ResultCpuUsageRateByDay responseDto = new ResultCpuUsageRateByDay(Collections.singletonList(usageRate));

        when(cpuMonitoringService.getCpuUsageRateByDay(any(GetCpuUsageRateByDay.class)))
                .thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring/get/day")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statsUsage[0].average").value(25.67))
                .andExpect(jsonPath("$.statsUsage[0].maximumUsage").value(40.89))
                .andExpect(jsonPath("$.statsUsage[0].minimumUsage").value(15.34));
    }

    @Test
    @DisplayName("일 단위 CPU 사용률 조회 - 데이터베이스 값 없음")
    void failGetCpuUsageRateByDay_NotFound() throws Exception {
        GetCpuUsageRateByDay requestDto = new GetCpuUsageRateByDay();
        ZonedDateTime insertDay = ZonedDateTime.now();
        ZonedDateTime insertDay2 = dateUtil.getSearchDay(10);
        requestDto.setStartDay(insertDay);
        requestDto.setEndDay(insertDay2);

        when(cpuMonitoringService.getCpuUsageRateByDay(any(GetCpuUsageRateByDay.class)))
                .thenThrow(new CustomException(NOT_FOUND_DATA));

        mockMvc.perform(MockMvcRequestBuilders.post("/monitoring/get/day")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND_DATA"))
                .andExpect(jsonPath("$.message").value(NOT_FOUND_DATA.getMessage()));
    }

}

