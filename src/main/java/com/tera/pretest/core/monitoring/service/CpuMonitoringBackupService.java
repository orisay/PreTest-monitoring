package com.tera.pretest.core.monitoring.service;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByDayBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByHourBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByMinuteBackup;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByDayBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByHourBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByMinuteBackupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.DELETE_FLAG;

@Log4j2
@AllArgsConstructor
@Service
public class CpuMonitoringBackupService {

    private final CpuUsageRateByMinuteBackupRepository cpuUsageRateByMinuteBackupRepository;
    private final CpuUsageRateByHourBackupRepository cpuUsageRateByHourBackupRepository;
    private final CpuUsageRateByDayBackupRepository cpuUsageRateByDayBackupRepository;


    public void backupCpuUsageStatsByMinute(List<CpuUsageRateByMinute> oldData){
        List<CpuUsageRateByMinuteBackup> backupData = CpuUsageRateByMinuteBackup.toBackupData(oldData);
        cpuUsageRateByMinuteBackupRepository.saveAll(backupData);
    }


    public void backupCpuUsageStatsByHour(List<CpuUsageRateByHour> oldData){
        List<CpuUsageRateByHourBackup> backupData = CpuUsageRateByHourBackup.toBackupData(oldData);
        cpuUsageRateByHourBackupRepository.saveAll(backupData);

    }

    public void backupCpuUsageStatsByDay(List<CpuUsageRateByDay> oldData){
        List<CpuUsageRateByDayBackup> backupData = CpuUsageRateByDayBackup.toBackupData(oldData);
        cpuUsageRateByDayBackupRepository.saveAll(backupData);

    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void hardDeleteOutdatedCpuUsageStatsByMinute() {
        cpuUsageRateByMinuteBackupRepository.deleteByFlag(DELETE_FLAG);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void hardDeleteOutdatedCpuUsageStatsByHour() {
        cpuUsageRateByHourBackupRepository.deleteByFlag(DELETE_FLAG);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void hardDeleteOutdatedCpuUsageStatsByDay() {
        cpuUsageRateByDayBackupRepository.deleteByFlag(DELETE_FLAG);
    }
}
