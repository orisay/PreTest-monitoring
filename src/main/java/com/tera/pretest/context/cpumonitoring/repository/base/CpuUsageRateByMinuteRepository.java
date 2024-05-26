package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface CpuUsageRateByMinuteRepository extends JpaRepository<CpuUsageRateByMinute, Long> {

    List<CpuUsageRateByMinute> findByCreateTimeBetween(Timestamp startTime, Timestamp endTime);

    @Modifying
    @Query("UPDATE CpuUsageRateByMinute stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    long softDeleteOldData(Timestamp pastDay);

    List<CpuUsageRateByMinute> findByFlag(String flag);

    long deleteByFlag(String flag);

}
