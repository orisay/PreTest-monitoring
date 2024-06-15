package com.tera.pretest.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.tera.pretest.core.constant.MonitoringConstant.TIME_ZONE;


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
