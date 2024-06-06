package com.tera.pretest.config;

import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.core.config.FormatterConfig;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.util.DateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
    @Bean(name = "DateUtilTest")
    public DateUtil testDateUtil(){
        return new DateUtil(testTimeProvider());
    }

    @Primary
    @Bean(name = "TimeProviderTest")
    public TimeProvider testTimeProvider(){
        return Mockito.mock(TimeProvider.class);
    }

    @Primary
    @Bean(name = "BuildFactoryTest")
    public BuildFactory testBuildFactory(){
        BuildFactory buildFactory = Mockito.mock(BuildFactory.class);
        return buildFactory;
    }

    @Primary
    @Bean(name = "ShutdownManagerTest")
    public ShutdownManager testShutdownManager(){
        return Mockito.mock(ShutdownManager.class);
    }

    @Primary
    @Bean(name="FormatterConfigTest")
    public FormatterConfig TestformatterConfig(){
        return Mockito.mock(FormatterConfig.class);
    }

}
