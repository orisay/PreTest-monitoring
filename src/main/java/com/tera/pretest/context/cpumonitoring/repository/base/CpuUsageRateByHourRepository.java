package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CpuUsageRateByHourRepository extends JpaRepository<CpuUsageRateByHour, Long> {
    List<CpuUsageRateByHour> findByCreateTimeBetween(LocalDateTime startDay, LocalDateTime endDay);
}
