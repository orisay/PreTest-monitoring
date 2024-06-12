package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface CpuUsageRateByHourRepository extends JpaRepository<CpuUsageRateByHour, Long> {

    @Query("SELECT stats FROM CpuUsageRateByHour stats WHERE stats.createTime BETWEEN :startDay AND :endDay and stats.flag = 'N'")
    List<CpuUsageRateByHour> findByCreateTimeBetween(@Param("startDay") ZonedDateTime startDay, @Param("endDay") ZonedDateTime endDay);

    @Modifying
    @Query("UPDATE CpuUsageRateByHour stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    long softDeleteOldData(@Param("pastDay") ZonedDateTime pastDay);

    List<CpuUsageRateByHour> findByFlag(String flag);

    long deleteByFlag(String flag);

}
