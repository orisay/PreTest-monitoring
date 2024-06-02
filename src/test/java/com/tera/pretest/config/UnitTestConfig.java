package com.tera.pretest.config;

import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.util.DateUtil;
import com.tera.pretest.core.util.TimeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UnitTestConfig {
    @Bean(name = "DateUtilTest")
    public DateUtil testDateUtil(){
        return new DateUtil(TimeProvider.getInstance());
    }

    @Bean(name = "TimeProviderTest")
    public TimeProvider testTimeProvider(){
        return TimeProvider.getInstance();
    }

    @Bean(name = "BuildFactoryTest")
    public BuildFactory testBuildFactory(){
        return new BuildFactory();
    }

    @Bean(name = "ShutdownManagerTest")
    public ShutdownManager testShutdownManager(){
        return  new ShutdownManager();
    }

}
