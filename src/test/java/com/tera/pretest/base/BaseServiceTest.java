package com.tera.pretest.base;

import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByDayBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByHourBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByMinuteBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.context.cpumonitoring.service.CpuMonitoringService;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.monitoring.service.CpuMonitoringManageService;
import com.tera.pretest.core.util.DateUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.hardware.CentralProcessor;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    @Mock
    protected CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Mock
    protected CpuUsageRateByHourRepository cpuUsageRateByHourRepository;

    @Mock
    protected CpuUsageRateByDayRepository cpuUsageRateByDayRepository;

    @Mock
    protected DateUtil dateUtil;

    @Mock
    protected CpuUsageRateByMinuteBackupRepository cpuUsageRateByMinuteBackupRepository;

    @Mock
    protected CpuUsageRateByHourBackupRepository cpuUsageRateByHourBackupRepository;

    @Mock
    protected CpuUsageRateByDayBackupRepository cpuUsageRateByDayBackupRepository;

    @Mock
    protected CentralProcessor centralProcessor;

    @InjectMocks
    protected CpuMonitoringBackupService cpuMonitoringBackupService;

    @InjectMocks
    protected CpuMonitoringManageService cpuMonitoringManageService;

    @InjectMocks
    protected CpuMonitoringService cpuMonitoringService;


}
