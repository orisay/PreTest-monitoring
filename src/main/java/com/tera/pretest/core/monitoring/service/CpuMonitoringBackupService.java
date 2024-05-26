package com.tera.pretest.core.monitoring.service;

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
import com.tera.pretest.core.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tera.pretest.core.exception.CustomExceptionCode.NOT_FOUND_DATA;
import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.DELETE_FLAG;

@Log4j2
@AllArgsConstructor
@Service
public class CpuMonitoringBackupService {

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;
    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;
    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;
    private final CpuUsageRateByMinuteBackupRepository cpuUsageRateByMinuteBackupRepository;
    private final CpuUsageRateByHourBackupRepository cpuUsageRateByHourBackupRepository;
    private final CpuUsageRateByDayBackupRepository cpuUsageRateByDayBackupRepository;
    private BuildFactory buildFactory;


    public void backupCpuUsageStatsByMinute(List<CpuUsageRateByMinute> oldData){
        List<CpuUsageRateByMinuteBackup> backupData = buildFactory.toBackupDataByMinuteStats(oldData);
        if(backupData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        cpuUsageRateByMinuteBackupRepository.saveAll(backupData);
    }


    public void backupCpuUsageStatsByHour(List<CpuUsageRateByHour> oldData){
        List<CpuUsageRateByHourBackup> backupData = buildFactory.toBackupDataByHourStats(oldData);
        if(backupData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        cpuUsageRateByHourBackupRepository.saveAll(backupData);

    }

    public void backupCpuUsageStatsByDay(List<CpuUsageRateByDay> oldData){
        List<CpuUsageRateByDayBackup> backupData = buildFactory.toBackupDataByDayStats(oldData);
        if(backupData.isEmpty())
            throw new CustomException(NOT_FOUND_DATA);
        cpuUsageRateByDayBackupRepository.saveAll(backupData);

    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void hardDeleteOutdatedCpuUsageStatsByMinute() {
        cpuUsageRateByMinuteRepository.deleteByFlag(DELETE_FLAG);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void hardDeleteOutdatedCpuUsageStatsByHour() {
        cpuUsageRateByHourRepository.deleteByFlag(DELETE_FLAG);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void hardDeleteOutdatedCpuUsageStatsByDay() {
        cpuUsageRateByDayRepository.deleteByFlag(DELETE_FLAG);
    }
}
