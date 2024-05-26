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
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.exception.CustomException;
import com.tera.pretest.core.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static com.tera.pretest.core.exception.CustomExceptionCode.NOT_FOUND_DATA;

@Log4j2
@AllArgsConstructor
@Service
public class CpuMonitoringService {
    private CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;
    private CpuUsageRateByHourRepository cpuUsageRateByHourRepository;
    private CpuUsageRateByDayRepository cpuUsageRateByDayRepository;
    private DateUtil dateUtil;

    @Transactional(readOnly = true)
    public ResultCpuUsageRateByMinute getCpuUsageRateByMinute(GetCpuUsageRateByMinute getCpuUsageRateByMinute) {
        Timestamp startDay = dateUtil.truncateTimestampToHour(getCpuUsageRateByMinute.getStartDay());
        Timestamp endDay = dateUtil.addOneHour(startDay);
        List<CpuUsageRateByMinute> statsData = cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startDay, endDay);
        if(statsData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return ResultCpuUsageRateByMinute.toBuild(statsData);
    }


    @Transactional(readOnly = true)
    public ResultCpuUsageRateByHour getCpuUsageRateByHour(GetCpuUsageRateByHour getCpuUsageRateByHour) {
        Timestamp startDay = dateUtil.truncateTimestampToDay(getCpuUsageRateByHour.getStartDay());
        Timestamp endDay = dateUtil.addOneDayByInputDay(startDay);
        List<CpuUsageRateByHour> statsData = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
        if(statsData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return ResultCpuUsageRateByHour.toBuild(statsData);
    }

    @Transactional(readOnly = true)
    public ResultCpuUsageRateByDay getCpuUsageRateByDay(GetCpuUsageRateByDay getCpuUsageRateByDay) {
        Timestamp startDay = dateUtil.truncateTimestampToDay(getCpuUsageRateByDay.getStartDay());
        Timestamp endDay = dateUtil.truncateTimestampToDay(getCpuUsageRateByDay.getEndDay());
        Timestamp exactEndDay = exactEndDayData(endDay);
        List<CpuUsageRateByDay> statsData = cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, exactEndDay);
        if(statsData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return ResultCpuUsageRateByDay.toBuild(statsData);
    }

    private Timestamp exactEndDayData(Timestamp endDay) {
        boolean isSameDay = dateUtil.isSameDay(endDay);
        if (!isSameDay)
            return endDay;
        return dateUtil.addOneDay(endDay);
    }

}
