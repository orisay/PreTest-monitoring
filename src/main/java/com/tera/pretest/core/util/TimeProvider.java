package com.tera.pretest.core.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

//    @Autowired
    private Clock clock;


    @Autowired
    public TimeProvider(Clock clock) {
        this.clock = clock;
        log.debug("Calling TimeProvider constructor");
        updateTime();
        scheduledExecutorService.scheduleAtFixedRate(this::updateTime, 0, UPDATE_INTERVAL_TIME, TimeUnit.MINUTES);
    }

    public void setClockFixedTime(Clock clock){
        this.clock = clock;
        updateTime();
    }

    public void updateTime() {
        log.debug("Calling updateTime");
        ZoneId zoneId = ZoneId.of(TIME_ZONE);
        ZonedDateTime nowZoneDateTime = ZonedDateTime.now(clock.withZone(zoneId));
        Timestamp nowTimestamp = Timestamp.from(nowZoneDateTime.toInstant());
        currentZonedDateTimeAt.set(nowZoneDateTime);
        currentTimestampAt.set(nowTimestamp);
        log.debug("TimeProvider updateTime first Log nowZoneDateTime:{}", nowZoneDateTime);
    }

    public ZonedDateTime getCurrentZonedDateTimeAt() {
        log.debug("Test currentZonedDateTimeAt Start");
        log.debug("Test currentZonedDateTimeAt.get():{}", currentZonedDateTimeAt.get());
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
