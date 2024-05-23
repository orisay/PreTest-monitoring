package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CpuUsageRateByMinuteRepository extends JpaRepository<CpuUsageRateByMinute, Long> {
    List<CpuUsageRateByMinute> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
