package com.tera.pretest.core.config;

import com.tera.pretest.core.util.TimeProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Component
public class TimeProviderListener implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TimeProviderListener.applicationContext = applicationContext;
    }

    public static ZonedDateTime getCurrentZonedDateTime(){
        TimeProvider timeProvider = applicationContext.getBean(TimeProvider.class);
        return timeProvider.getCurrentZonedDateTimeAt();
    }

    public static Timestamp getCurrentTimeStamp(){
        TimeProvider timeProvider = applicationContext.getBean(TimeProvider.class);
        return  timeProvider.getCurrentTimestampAt();

    }

}
