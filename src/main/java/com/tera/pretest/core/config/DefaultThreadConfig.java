package com.tera.pretest.core.config;

import com.tera.pretest.core.config.abstracts.ThreadConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
public class DefaultThreadConfig extends ThreadConfig {

    @Override
    @Bean(name = "daemonThreadForAsync")
    public Executor createThreadPool() {
        return super.createThreadPool();
    }

    @Override
    public void sleepThread(long time) throws InterruptedException {
        super.sleepThread(time);
    }
}
