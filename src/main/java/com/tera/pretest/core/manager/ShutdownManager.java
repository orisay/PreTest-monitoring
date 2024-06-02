package com.tera.pretest.core.manager;

import com.tera.pretest.core.monitoring.CpuMonitoring;
import com.tera.pretest.core.util.TimeProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@NoArgsConstructor
@AllArgsConstructor
@Component
public class ShutdownManager implements ApplicationListener<ContextClosedEvent> {
    private TimeProvider timeProvider;
    private CpuMonitoring cpuMonitoring;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

    }

    @PreDestroy
    public void shutdown() {
        timeProvider.shutdown();
        cpuMonitoring.shutdown();
    }
}
