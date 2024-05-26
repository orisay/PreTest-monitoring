package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface CpuUsageRateByHourRepository extends JpaRepository<CpuUsageRateByHour, Long> {

    List<CpuUsageRateByHour> findByCreateTimeBetween(Timestamp startDay, Timestamp endDay);

    @Modifying
    @Query("UPDATE CpuUsageRateByHour stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    long softDeleteOldData(Timestamp pastDay);

    List<CpuUsageRateByHour> findByFlag(String flag);

    long deleteByFlag(String flag);

}
