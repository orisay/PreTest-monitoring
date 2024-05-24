package com.tera.pretest.context.cpumonitoring.repository.backup;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByHourBackup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuUsageRateByHourBackupRepository extends JpaRepository<CpuUsageRateByHourBackup, Long> {

    long deleteByFlag(String flag);
}
