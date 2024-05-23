package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuUsageRateByHourRepository extends JpaRepository<CpuUsageRateByHour, Long> {
}
