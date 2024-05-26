package com.tera.pretest.core.util;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.ONE_DAY;
import static com.tera.pretest.core.monitoring.contant.MonitoringConstant.ONE_HOUR;

@Component
public class DateUtil {

    public Timestamp truncateTimestampToHour(Timestamp choiceDayAndHour) {
        ZonedDateTime hourTruncate = choiceDayAndHour.toInstant()
                .truncatedTo(ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault());
        return Timestamp.from(hourTruncate.toInstant());
    }

    public Timestamp truncateTimestampToDay(Timestamp choiceDay) {
        ZonedDateTime hourTruncate = choiceDay.toInstant()
                .truncatedTo(ChronoUnit.DAYS)
                .atZone(ZoneId.systemDefault());
        return Timestamp.from(hourTruncate.toInstant());
    }

    public Timestamp addOneDay(Timestamp inputDay){
        ZonedDateTime today = inputDay.toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime addOneDay = today.plusDays(ONE_DAY);
        return Timestamp.from(addOneDay.toInstant());
    }

    public Timestamp addOneHour(Timestamp inputDay){
        ZonedDateTime startDay = inputDay.toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime addOneHour = startDay.plusHours(ONE_HOUR);
        return  Timestamp.from(addOneHour.toInstant());
    }

    public Timestamp addOneDayByInputDay(Timestamp inputDay){
        ZonedDateTime day = inputDay.toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime addOneDay = day.plusDays(ONE_DAY);
        return  Timestamp.from(addOneDay.toInstant());
    }


    public boolean isSameDay(Timestamp inputDay){
        LocalDate today = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate choiceDay = inputDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.equals(choiceDay);
    }


    public Timestamp getTodayTruncatedToHour() {
        return Timestamp.from(ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).toInstant());
    }

    public Timestamp getTodayTruncatedToDay() {
        return Timestamp.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant());
    }

    public Timestamp getSearchHour(Integer hourToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusHours(hourToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }

    public Timestamp getSearchDay(Integer daysToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusDays(daysToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }

    public Timestamp getSearchMonth(Integer monthToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusMonths(monthToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }

    public Timestamp getSearchYear(Integer yearToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime searchDay = todayTruncated.minusDays(yearToSubtract);
        return Timestamp.from(searchDay.toInstant());
    }
}
