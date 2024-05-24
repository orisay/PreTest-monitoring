package com.tera.pretest.context.cpumonitoring.repository.backup;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByDayBackup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuUsageRateByDayBackupRepository extends JpaRepository<CpuUsageRateByDayBackup, Long> {

    long deleteByFlag(String flag);
}
