package com.tera.pretest.core.config;

import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Log4j2
@Component
public class TimeProviderListener implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TimeProviderListener.applicationContext = applicationContext;
    }

    public static ZonedDateTime getCurrentZonedDateTime(){
        log.debug("applicationContext.getBean(TimeProvider.class):{}", applicationContext.getBean(TimeProvider.class));
        TimeProvider timeProvider = applicationContext.getBean(TimeProvider.class);
        log.debug("getCurrentZonedDateTime timeProvider.getCurrentZonedDateTimeAt() :{}",timeProvider.getCurrentZonedDateTimeAt());
        return timeProvider.getCurrentZonedDateTimeAt();
    }

    public static Timestamp getCurrentTimeStamp(){
        TimeProvider timeProvider = applicationContext.getBean(TimeProvider.class);
        return  timeProvider.getCurrentTimestampAt();

    }

}
