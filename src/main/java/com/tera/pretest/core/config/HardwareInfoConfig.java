package com.tera.pretest.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Configuration
public class HardwareInfoConfig {

    @Bean
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }

    @Bean
    public CentralProcessor centralProcessor(SystemInfo systemInfo) {
        return systemInfo.getHardware().getProcessor();
    }
}
