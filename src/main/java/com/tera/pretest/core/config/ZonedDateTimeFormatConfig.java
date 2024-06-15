package com.tera.pretest.core.config;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.tera.pretest.core.constant.MonitoringConstant.TIME_ZONE;


@Log4j2
@NoArgsConstructor
@Configuration
public class ZonedDateTimeFormatConfig {

    @Value("${app.datetime-format}")
    private String zonedDateTimeFormat;

    public DateTimeFormatter dateTimeFormatter;


    @PostConstruct
    public void initZoneDateTimeFormatter() {
        log.info("init 1st work initZoneDateTimeFormatter before dateTimeFormatter:{}", dateTimeFormatter);
        log.info("init 2nd work initZoneDateTimeFormatter before need dateTimeFormatter:{}", zonedDateTimeFormat);
        dateTimeFormatter = DateTimeFormatter.ofPattern(zonedDateTimeFormat);
        log.info("init 3th work initZoneDateTimeFormatter after dateTimeFormatter:{}", dateTimeFormatter);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        log.info("4th return DateTimeFormatter bean: {}", dateTimeFormatter);
        return dateTimeFormatter;
    }

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(TIME_ZONE));
    }

    @Bean
    @Profile("totalTest")
    public Clock testClock() {
        Instant fixedInstant = LocalDateTime.parse("2024-05-23T01:00:00.000").atZone(ZoneId.of("Asia/Seoul")).toInstant();
        return Clock.fixed(fixedInstant, ZoneId.of("Asia/Seoul"));
    }

}
