package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuUsageRateByMinuteRepository extends JpaRepository<CpuUsageRateByMinute, Long> {
}
