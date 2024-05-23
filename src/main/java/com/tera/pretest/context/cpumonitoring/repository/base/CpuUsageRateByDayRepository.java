package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuUsageRateByDayRepository extends JpaRepository<CpuUsageRateByDay,Long> {
}
