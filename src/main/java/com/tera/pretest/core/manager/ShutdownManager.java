package com.tera.pretest.core.manager;

import com.tera.pretest.core.monitoring.CpuMonitoring;
import com.tera.pretest.core.util.TimeProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ShutdownManager implements ApplicationListener<ContextClosedEvent> {
    private final TimeProvider timeProvider;
    private final CpuMonitoring cpuMonitoring;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        timeProvider.shutdown();
        cpuMonitoring.shutdown();
    }
}
