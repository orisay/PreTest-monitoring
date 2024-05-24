package com.tera.pretest.context.cpumonitoring.repository.backup;

import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByMinuteBackup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuUsageRateByMinuteBackupRepository extends JpaRepository<CpuUsageRateByMinuteBackup, Long> {

    long deleteByFlag(String flag);
}
