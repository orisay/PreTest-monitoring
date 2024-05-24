package com.tera.pretest.context.cpumonitoring.repository.base;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface CpuUsageRateByDayRepository extends JpaRepository<CpuUsageRateByDay,Long> {

    @Modifying
    @Query("UPDATE CpuUsageRateByDay stats SET stats.flag = 'Y' WHERE stats.createTime < :pastDay")
    long softDeleteOldData(Timestamp pastDay);

    List<CpuUsageRateByDay> findByFlag(String flag);

}
