package com.tera.pretest.context.cpumonitoring.service;

import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.input.GetCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.dto.output.ResultCpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CpuMonitoringService {
    private CpuUsageRateByMinute cpuUsageRateByMinute;
    private CpuUsageRateByHour cpuUsageRateByHour;
    private CpuUsageRateByDay cpuUsageRateByDay;

    @Transactional(readOnly = true)
    public ResultCpuUsageRateByMinute getCpuUsageRateByMinute(GetCpuUsageRateByMinute getCpuUsageRateByMinute) {
        return null;
    }

    @Transactional(readOnly = true)
    public ResultCpuUsageRateByHour getCpuUsageRateByHour(GetCpuUsageRateByHour getCpuUsageRateByHour) {
        return null;
    }

    @Transactional(readOnly = true)
    public ResultCpuUsageRateByDay getCpuUsageRateByDay(GetCpuUsageRateByDay getCpuUsageRateByDay) {
        return null;
    }
}
