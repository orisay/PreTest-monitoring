package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface CpuUsageRateByMinuteRepository extends JpaRepository<CpuUsageRateByMinute, Long> {

    @Query("SELECT stats FROM CpuUsageRateByMinute stats WHERE stats.createTime BETWEEN :startTime AND :endTime and stats.flag = 'N'")
    List<CpuUsageRateByMinute> findByCreateTimeBetween(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    @Modifying
    @Query("UPDATE CpuUsageRateByMinute stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    int softDeleteOldData(@Param("pastDay") ZonedDateTime pastDay);

    List<CpuUsageRateByMinute> findByFlag(String flag);

    long deleteByFlag(String flag);

}
