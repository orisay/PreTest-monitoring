package com.tera.pretest.context.cpumonitoring.controller;

import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log4j2
@Tag(name="CPU Monitoring Search", description = "모니터링 조회")
@RequiredArgsConstructor
@RequestMapping("/monitoring")
@RestController
public class CpuMonitoringController {

    private final CpuMonitoringService cpuMonitoringService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "분 단위 CPU 사용률 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 유효성 검사 실패"),
            @ApiResponse(responseCode = "404", description = "DB에 값이 존재하지 않거나 잘못 된 값을 반환 할 때")
    })
    @Operation(summary = "CPU Average Usage Rate",description = "10초씩 6번 계산한 CPU 사용률의 1분 평균값 반환")
    @PostMapping("/get/minute")
    public ResponseEntity<ResultCpuUsageRateByMinute> getCpuUsageRateByMinute(@RequestBody @Valid GetCpuUsageRateByMinute getCpuUsageRateByMinute){
        ResultCpuUsageRateByMinute resultCpuUsageRateByMinute = cpuMonitoringService.getCpuUsageRateByMinute(getCpuUsageRateByMinute);
        return ResponseEntity.ok(resultCpuUsageRateByMinute);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "시 단위 CPU 사용률 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 유효성 검사 실패"),
            @ApiResponse(responseCode = "404", description = "DB에 값이 존재하지 않거나 잘못 된 값을 반환 할 때")
    })
    @Operation(summary = "CPU Average Usage Rate By One Hour",description = "1분씩 60번의 계산한 CPU 사용률의 1시간 평균값, 최대 사용률, 최소 사용률 반환")
    @PostMapping("/get/hour")
    public ResponseEntity<ResultCpuUsageRateByHour> getCpuUsageRateByHour(@RequestBody @Valid GetCpuUsageRateByHour getCpuUsageRateByHour){
        ResultCpuUsageRateByHour resultCpuUsageRateByHour = cpuMonitoringService.getCpuUsageRateByHour(getCpuUsageRateByHour);
        return ResponseEntity.ok(resultCpuUsageRateByHour);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일 단위 CPU 사용률 조회 성공"),
            @ApiResponse(responseCode = "400", description = "파라미터 유효성 검사 실패"),
            @ApiResponse(responseCode = "404", description = "DB에 값이 존재하지 않거나 잘못 된 값을 반환 할 때")
    })
    @Operation(summary = "CPU Average Usage Rate By One Day",description = "1시간 씩 24번의 계산한 CPU 사용률의 하루 평균값, 최대 사용률, 최소 사용률 반환")
    @PostMapping("/get/day")
    public ResponseEntity<ResultCpuUsageRateByDay> getCpuUsageRateByDay(@RequestBody @Valid GetCpuUsageRateByDay getCpuUsageRateByDay){
        ResultCpuUsageRateByDay resultCpuUsageRateByDay = cpuMonitoringService.getCpuUsageRateByDay(getCpuUsageRateByDay);
        return ResponseEntity.ok(resultCpuUsageRateByDay);
    }
}
