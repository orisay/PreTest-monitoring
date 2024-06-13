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
import com.tera.pretest.core.exception.restful.CustomException;
import com.tera.pretest.core.util.ProviderDateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.tera.pretest.core.exception.restful.CustomExceptionCode.NOT_FOUND_DATA;

@Log4j2
@AllArgsConstructor
@Service
public class CpuMonitoringService {

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    private final ProviderDateUtil dateUtil;


    @Transactional(readOnly = true)
    public ResultCpuUsageRateByMinute getCpuUsageRateByMinute(GetCpuUsageRateByMinute getCpuUsageRateByMinute) {
        ZonedDateTime startTime = dateUtil.truncateZonedDateTimeToHour(getCpuUsageRateByMinute.getStartTime());
        ZonedDateTime endTime = dateUtil.addOneHour(startTime);
        log.info("Service method startTime: {} , endTime: {}", startTime, endTime);
        List<CpuUsageRateByMinute> statsData = cpuUsageRateByMinuteRepository.findByCreateTimeBetween(startTime, endTime);
        if(statsData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return ResultCpuUsageRateByMinute.toBuild(statsData);
    }


    @Transactional(readOnly = true)
    public ResultCpuUsageRateByHour getCpuUsageRateByHour(GetCpuUsageRateByHour getCpuUsageRateByHour) {
        ZonedDateTime startDay = dateUtil.truncateZonedDateTimeToDay(getCpuUsageRateByHour.getStartDay());
        ZonedDateTime endDay = dateUtil.addOneDayByInputDay(startDay);
        List<CpuUsageRateByHour> statsData = cpuUsageRateByHourRepository.findByCreateTimeBetween(startDay, endDay);
        if(statsData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return ResultCpuUsageRateByHour.toBuild(statsData);
    }

    @Transactional(readOnly = true)
    public ResultCpuUsageRateByDay getCpuUsageRateByDay(GetCpuUsageRateByDay getCpuUsageRateByDay) {
        ZonedDateTime startDay = dateUtil.truncateZonedDateTimeToDay(getCpuUsageRateByDay.getStartDay());
        ZonedDateTime endDay = dateUtil.truncateZonedDateTimeToDay(getCpuUsageRateByDay.getEndDay());
        ZonedDateTime exactEndDay = exactEndDayData(endDay);
        List<CpuUsageRateByDay> statsData = cpuUsageRateByDayRepository.findByCreateTimeBetween(startDay, exactEndDay);
        if(statsData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        return ResultCpuUsageRateByDay.toBuild(statsData);
    }

    private ZonedDateTime exactEndDayData(ZonedDateTime endDay) {
        boolean isSameDay = dateUtil.isSameDay(endDay);
        if (!isSameDay)
            return endDay;
        return dateUtil.addOneDay(endDay);
    }

}
