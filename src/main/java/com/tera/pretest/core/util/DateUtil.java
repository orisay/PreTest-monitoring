package com.tera.pretest.core.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
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
    public ZonedDateTime truncateTimestampToHour(ZonedDateTime choiceDayAndHour) {
        return choiceDayAndHour.truncatedTo(ChronoUnit.HOURS);
    }

    // Test Code 변경시 바로 아래 함수로 이동
    public Timestamp truncateTimestampToDay(Timestamp choiceDay) {
        ZonedDateTime hourTruncate = choiceDay.toInstant()
                .truncatedTo(ChronoUnit.DAYS)
                .atZone(ZoneId.of(TIME_ZONE));
        return Timestamp.from(hourTruncate.toInstant());
    }
    //TEMP
    public ZonedDateTime truncateTimestampToDay(ZonedDateTime choiceDay) {
        return choiceDay.truncatedTo(ChronoUnit.DAYS);
    }



    public ZonedDateTime addOneDay(ZonedDateTime inputDay){
        return inputDay.plusDays(ONE_DAY);
    }

    public ZonedDateTime addOneHour(ZonedDateTime inputDay){
        return  inputDay.plusHours(ONE_HOUR);
    }

    public ZonedDateTime addOneDayByInputDay(ZonedDateTime inputDay){
        return  inputDay.plusDays(ONE_DAY);
    }


    public boolean isSameDay(ZonedDateTime inputDay){
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
