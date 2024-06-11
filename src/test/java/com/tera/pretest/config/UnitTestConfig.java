package com.tera.pretest.config;

import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.monitoring.CpuMonitoring;
import com.tera.pretest.core.util.ProviderDateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@Log4j2
@TestConfiguration
public class UnitTestConfig {

    @Value("${app.datetime-format:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}")
    private String zonedDateTimeFormat;


    @Primary
    @Bean
//    @Bean(name = "DateUtilTest")
    public ProviderDateUtil testDateUtil(TimeProvider timeProvider){
        ProviderDateUtil mockDateUtil = Mockito.mock(ProviderDateUtil.class);
        return mockDateUtil;
    }


    @Primary
    @Bean
//    @Bean(name = "TimeProviderTest")
    public TimeProvider testTimeProvider(){
        return Mockito.mock(TimeProvider.class);
    }

    @Primary
    @Bean
    public CpuMonitoring mockCpuMonitoring() {
        return Mockito.mock(CpuMonitoring.class);
    }

    @Primary
    @Bean
//    @Bean(name = "ShutdownManagerTest")
    public ShutdownManager testShutdownManager(TimeProvider timeProvider, CpuMonitoring cpuMonitoring) {
        return new ShutdownManager(timeProvider, cpuMonitoring);
    }

    @Primary
    @Bean
//    @Bean(name = "BuildFactoryTest")
    public BuildFactory testBuildFactory(){
        BuildFactory buildFactory = Mockito.mock(BuildFactory.class);
        return buildFactory;
    }


    @Primary
    @Bean
//    @Bean(name="FormatterConfigTest")
    public FormatterConfig TestformatterConfig(){
        return Mockito.mock(FormatterConfig.class);
    }

}
