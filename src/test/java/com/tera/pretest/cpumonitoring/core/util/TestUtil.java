package com.tera.pretest.cpumonitoring.core.util;

import java.text.DecimalFormat;

public class TestUtil {

    public static Double changeDecimalFormatCpuUsage(Double cpuUsage) {
        DecimalFormat roundUsage = new DecimalFormat("0.00");
        return Double.parseDouble(roundUsage.format(cpuUsage));
    }
}
