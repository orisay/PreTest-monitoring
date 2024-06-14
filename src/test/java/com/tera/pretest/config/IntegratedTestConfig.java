package com.tera.pretest.config;

import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.monitoring.CpuMonitoring;
import com.tera.pretest.core.util.ProviderDateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.tera.pretest.core.contant.MonitoringConstant.TIME_ZONE;


@Log4j2
@Profile("totalTest")
@TestConfiguration
public class IntegratedTestConfig {

    @Primary
    @Bean(name="FixedTestClock")
    public Clock fixedClock() {
        Instant fixedInstant = LocalDateTime.parse("2024-05-24T00:00:00.000").atZone(ZoneId.of(TIME_ZONE)).toInstant();
        return Clock.fixed(fixedInstant, ZoneId.of(TIME_ZONE));
    }
    @Bean(name = "basicClock")
    public Clock systemDefaultClock() {
        return Clock.systemDefaultZone();
    }

}
