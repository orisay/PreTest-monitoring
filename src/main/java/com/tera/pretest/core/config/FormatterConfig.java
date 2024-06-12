package com.tera.pretest.core.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;

@Log4j2
@Configuration
public class FormatterConfig {

    private static DecimalFormat decimalFormat;


    @PostConstruct
    public void initFormatterConfig() {
        decimalFormat= new DecimalFormat("0.00");

    }

    public Double changeDecimalFormatCpuUsage(Double cpuUsage) {
        log.info("changeDecimalFormatCpuUsage cpuUsage :{}, and decimalFormat:{}", cpuUsage, decimalFormat);
        return Double.parseDouble(decimalFormat.format(cpuUsage));
    }

}
