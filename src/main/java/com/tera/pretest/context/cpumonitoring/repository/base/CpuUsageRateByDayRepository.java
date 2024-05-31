package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

public interface CpuUsageRateByDayRepository extends JpaRepository<CpuUsageRateByDay,Long> {

    List<CpuUsageRateByDay> findByCreateTimeBetween(Timestamp startDay, Timestamp endDay);

    @Modifying
    @Query("UPDATE CpuUsageRateByDay stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    long softDeleteOldData(ZonedDateTime pastDay);

    List<CpuUsageRateByDay> findByFlag(String flag);

    long deleteByFlag(String flag);

}
