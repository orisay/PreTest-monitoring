package com.tera.pretest.context.cpumonitoring.controller;

import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Log4j2
@RequiredArgsConstructor
@RequestMapping("/Monitoring")
@RestController
public class CpuMonitoringController {
    //TODO 여기서 잘못된 파라미터에 대한 예외 처리 로그 DTO에서만 처리
    private CpuMonitoringService cpuMonitoringService;

    @GetMapping("/get/minute")
    public ResponseEntity<?> getCpuUsageRateByMinute(@RequestBody @Valid GetCpuUsageRateByMinute getCpuUsageRateByMinute){
        ResultCpuUsageRateByMinute resultCpuUsageRateByMinute = cpuMonitoringService.getCpuUsageRateByMinute(getCpuUsageRateByMinute);
        return null;
    }

    @GetMapping("/get/hour")
    public ResponseEntity<?> getCpuUsageRateByHour(@RequestBody @Valid GetCpuUsageRateByHour getCpuUsageRateByHour){
        ResultCpuUsageRateByHour resultCpuUsageRateByHour = cpuMonitoringService.getCpuUsageRateByHour(getCpuUsageRateByHour);
        return null;
    }

    @GetMapping("/get/day")
    public ResponseEntity<?> getCpuUsageRateByDay(@RequestBody @Valid GetCpuUsageRateByDay getCpuUsageRateByDay){
        ResultCpuUsageRateByDay resultCpuUsageRateByDay = cpuMonitoringService.getCpuUsageRateByDay(getCpuUsageRateByDay);
        return null;
    }
}
