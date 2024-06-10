package com.tera.pretest.core.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.tera.pretest.core.contant.MonitoringConstant.*;

@Log4j2
@Component
public class TimeProvider {


    private final AtomicReference<ZonedDateTime> currentZonedDateTimeAt = new AtomicReference<>();

    private AtomicReference<Timestamp> currentTimestampAt = new AtomicReference<>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    public TimeProvider() {
        log.info("TimeProvider start Log");
        log.info("TimeProvider second Log");
        log.info("TimeProvider third Log");
        scheduledExecutorService.scheduleAtFixedRate(this::updateTime, 0, UPDATE_INTERVAL_TIME, TimeUnit.MINUTES);
    }





    public void updateTime() {
        ZonedDateTime nowZoneDateTime = ZonedDateTime.now(ZoneId.of(TIME_ZONE));
        Timestamp nowTimestamp = Timestamp.from(nowZoneDateTime.toInstant());
        currentZonedDateTimeAt.set(nowZoneDateTime);
        currentTimestampAt.set(nowTimestamp);
        log.info("TimeProvider updateTime first Log nowZoneDateTime:{}", nowZoneDateTime);
    }

    public ZonedDateTime getCurrentZonedDateTimeAt() {
        log.info("Test currentZonedDateTimeAt Start");
        log.info("Test currentZonedDateTimeAt.get():{}", currentZonedDateTimeAt.get());
        return currentZonedDateTimeAt.get();
    }

    public Timestamp getCurrentTimestampAt() {
        return currentTimestampAt.get();
    }


    public void shutdown() {
        shutdownService();
        try {
            if (!awaitShutdown()) {
                shutdownService();
            }
            if(!awaitShutdown())
                log.error("scheduledExecutorService 종료하지 못했습니다.");
        } catch (InterruptedException exception) {
            shutdownService();
            Thread.currentThread().interrupt();
        }
    }

    private void shutdownService() {
        scheduledExecutorService.shutdown();
    }

    private boolean awaitShutdown() throws InterruptedException {
        return scheduledExecutorService.awaitTermination(TEN_SECOND, TimeUnit.SECONDS);
    }


}
