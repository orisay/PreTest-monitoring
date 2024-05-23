package com.tera.pretest.core.monitoring.factory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.CPU_MONITORING_THREAD;

public class CpuMonitoringFactory implements ThreadFactory {

    private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(Runnable runnable) {
        Thread cpuMonitoringThread = threadFactory.newThread(runnable);
        cpuMonitoringThread.setName(CPU_MONITORING_THREAD);
        cpuMonitoringThread.setDaemon(true);
        //TODO Exception Handler aop or here
        return cpuMonitoringThread;
    }
}
