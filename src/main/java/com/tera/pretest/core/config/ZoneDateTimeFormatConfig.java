package com.tera.pretest.core.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Configuration
public class ZoneDateTimeFormatConfig {

    @Value("${app.datetime-format}")
    private final String zoneDateTimeFormat;

    public static DateTimeFormatter dateTimeFormatter;

    @PostConstruct
    public void initZoneDateTimeFormatter(){
        dateTimeFormatter = DateTimeFormatter.ofPattern(zoneDateTimeFormat);
    }

}
