package com.tera.pretest.config;

import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.monitoring.CpuMonitoring;
import com.tera.pretest.core.util.ProviderDateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@Log4j2
//@TestConfiguration
public class UnitTestConfig {

    @Value("${app.datetime-format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String zonedDateTimeFormat;


    @Primary
    @Bean
    public ProviderDateUtil testDateUtil(TimeProvider timeProvider){
        return new ProviderDateUtil(timeProvider);
    }


    @Primary
    @Bean
    public TimeProvider testTimeProvider(){
        return new TimeProvider();
    }

    @Primary
    @Bean
    public ShutdownManager testShutdownManager(TimeProvider timeProvider, CpuMonitoring cpuMonitoring) {
        return new ShutdownManager(timeProvider, cpuMonitoring);
    }

    @Primary
    @Bean
    public BuildFactory testBuildFactory(){
        return new BuildFactory();
    }


    @Primary
    @Bean
    public FormatterConfig TestformatterConfig(){
        return new FormatterConfig();
    }

}
