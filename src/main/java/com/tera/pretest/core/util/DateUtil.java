package com.tera.pretest.core.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.tera.pretest.core.contant.MonitoringConstant.*;

@AllArgsConstructor
@Component
public class DateUtil {

    private final TimeProvider provider;


    //현재 테스트 코드에서 사용중
    public Timestamp truncateTimestampToHour(Timestamp choiceDayAndHour) {
        ZonedDateTime hourTruncate = choiceDayAndHour.toInstant()
                .truncatedTo(ChronoUnit.HOURS)
                .atZone(ZoneId.of(TIME_ZONE));
        return Timestamp.from(hourTruncate.toInstant());
    }

    //TEMP
    public Timestamp truncateTimestampToHour(ZonedDateTime choiceDayAndHour) {
        ZonedDateTime hourTruncate = choiceDayAndHour
                .truncatedTo(ChronoUnit.HOURS);
        return Timestamp.from(hourTruncate.toInstant());
    }

    public Timestamp truncateTimestampToDay(Timestamp choiceDay) {
        ZonedDateTime hourTruncate = choiceDay.toInstant()
                .truncatedTo(ChronoUnit.DAYS)
                .atZone(ZoneId.of(TIME_ZONE));
        return Timestamp.from(hourTruncate.toInstant());
    }
    //TEMP
    public Timestamp truncateTimestampToDay(ZonedDateTime choiceDay) {
        ZonedDateTime hourTruncate = choiceDay.truncatedTo(ChronoUnit.DAYS);
        return Timestamp.from(hourTruncate.toInstant());
    }



    public Timestamp addOneDay(Timestamp inputDay){
        ZonedDateTime today = inputDay.toInstant().atZone(ZoneId.of(TIME_ZONE));
        ZonedDateTime addOneDay = today.plusDays(ONE_DAY);
        return Timestamp.from(addOneDay.toInstant());
    }

    public Timestamp addOneHour(Timestamp inputDay){
        ZonedDateTime startDay = inputDay.toInstant().atZone(ZoneId.of(TIME_ZONE));
        ZonedDateTime addOneHour = startDay.plusHours(ONE_HOUR);
        return  Timestamp.from(addOneHour.toInstant());
    }

    public Timestamp addOneDayByInputDay(Timestamp inputDay){
        ZonedDateTime day = inputDay.toInstant().atZone(ZoneId.of(TIME_ZONE));
        ZonedDateTime addOneDay = day.plusDays(ONE_DAY);
        return  Timestamp.from(addOneDay.toInstant());
    }


    public boolean isSameDay(Timestamp inputDay){
        ZonedDateTime today = getTodayTruncatedToDay();
        return today.equals(inputDay);
    }


    public ZonedDateTime getTodayTruncatedToHour() {
        return provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.HOURS);
    }

    public ZonedDateTime getTodayTruncatedToDay() {
        return provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.DAYS);
    }

    public ZonedDateTime getSearchHour(Integer hourToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay().toInstant().atZone(ZoneId.of(TIME_ZONE));
        return todayTruncated.minusHours(hourToSubtract);
    }

    public ZonedDateTime getSearchDay(Integer daysToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        return todayTruncated.minusDays(daysToSubtract);
    }

    public ZonedDateTime getSearchMonth(Integer monthToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        return todayTruncated.minusMonths(monthToSubtract);
    }

    public ZonedDateTime getSearchYear(Integer yearToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        return todayTruncated.minusDays(yearToSubtract);
    }
}
