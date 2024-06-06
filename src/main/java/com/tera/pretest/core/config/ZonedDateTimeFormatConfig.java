package com.tera.pretest.core.config;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;


@Log4j2
@NoArgsConstructor
@Configuration
public class ZonedDateTimeFormatConfig {

    @Value("${app.datetime-format}")
    private String zonedDateTimeFormat;

    public static DateTimeFormatter dateTimeFormatter;


    @PostConstruct
    public void initZoneDateTimeFormatter(){
        log.info("init 1st work initZoneDateTimeFormatter before dateTimeFormatter:{}",dateTimeFormatter);
        log.info("init 2nd work initZoneDateTimeFormatter before need dateTimeFormatter:{}",zonedDateTimeFormat);
        dateTimeFormatter = DateTimeFormatter.ofPattern(zonedDateTimeFormat);
        log.info("init 3th work initZoneDateTimeFormatter after dateTimeFormatter:{}",dateTimeFormatter);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter(){
        log.info("4th return DateTimeFormatter bean: {}", dateTimeFormatter);
        return dateTimeFormatter;
    }

}
