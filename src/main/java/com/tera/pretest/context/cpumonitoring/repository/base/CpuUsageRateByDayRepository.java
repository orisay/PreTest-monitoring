package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface CpuUsageRateByDayRepository extends JpaRepository<CpuUsageRateByDay, Long> {

    @Query("SELECT stats FROM CpuUsageRateByDay stats WHERE stats.createTime BETWEEN :startDay AND :endDay and stats.flag = 'N'")
    List<CpuUsageRateByDay> findByCreateTimeBetween(@Param("startDay") ZonedDateTime startDay, @Param("endDay") ZonedDateTime endDay);

    @Modifying
    @Query("UPDATE CpuUsageRateByDay stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    long softDeleteOldData(@Param("pastDay") ZonedDateTime pastDay);

    List<CpuUsageRateByDay> findByFlag(String flag);

    long deleteByFlag(String flag);

}
