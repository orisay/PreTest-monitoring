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
import com.tera.pretest.core.exception.process.ProcessCustomException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;

import static com.tera.pretest.core.contant.MonitoringConstant.DELETE_FLAG;
import static com.tera.pretest.core.exception.process.ProcessCustomExceptionCode.NOT_FOUND_DATA;

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
    private final BuildFactory buildFactory;


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> backupCpuUsageStatsByMinute(List<CpuUsageRateByMinute> oldData){
        log.info("backupCpuUsageStatsByMinute oldData : {}",oldData);
        List<CpuUsageRateByMinuteBackup> backupData = buildFactory.toBackupDataByMinuteStats(oldData);
        log.info("backupCpuUsageStatsByMinute backupData : {}",backupData);
        if(backupData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuUsageRateByMinuteBackupRepository.saveAll(backupData);
        return AsyncResult.forValue(null);
    }


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> backupCpuUsageStatsByHour(List<CpuUsageRateByHour> oldData){
        List<CpuUsageRateByHourBackup> backupData = buildFactory.toBackupDataByHourStats(oldData);
        if(backupData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuUsageRateByHourBackupRepository.saveAll(backupData);
        return AsyncResult.forValue(null);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Future<Void> backupCpuUsageStatsByDay(List<CpuUsageRateByDay> oldData){
        List<CpuUsageRateByDayBackup> backupData = buildFactory.toBackupDataByDayStats(oldData);
        if(backupData.isEmpty())
            throw new ProcessCustomException(NOT_FOUND_DATA);
        cpuUsageRateByDayBackupRepository.saveAll(backupData);
        return AsyncResult.forValue(null);

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
