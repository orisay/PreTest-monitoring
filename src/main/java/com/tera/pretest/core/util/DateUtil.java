package com.tera.pretest.core.util;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public Timestamp getTodayTruncatedToHour(){
        return Timestamp.from(ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).toInstant());
    }

    public Timestamp getTodayTruncatedToDay(){
        return Timestamp.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant());
    }

    public Timestamp getSearchHour(Integer hourToSubtract){
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusHours(hourToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }

    public Timestamp getSearchDay(Integer daysToSubtract){
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusDays(daysToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }

    public Timestamp getSearchMonth(Integer monthToSubtract){
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusMonths(monthToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }

    public Timestamp getSearchYear(Integer yearToSubtract){
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusDays(yearToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }
}
