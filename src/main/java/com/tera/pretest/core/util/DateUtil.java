package com.tera.pretest.core.util;

import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.tera.pretest.core.contant.MonitoringConstant.*;

@Log4j2
@Component
public class DateUtil {

    public static TimeProvider provider;

    @Autowired
    public DateUtil(TimeProvider provider) {
        log.info("1st DateUtil DI Test provider:{}", provider);
        this.provider = provider;
    }


    public ZonedDateTime truncateZonedDateTimeToHour(ZonedDateTime choiceDayAndHour) {
        log.info("DateUtil choiceDayAndHour : {}", choiceDayAndHour);
        return choiceDayAndHour.truncatedTo(ChronoUnit.HOURS);
    }

    public ZonedDateTime truncateZonedDateTimeToDay(ZonedDateTime choiceDay) {
        return choiceDay.truncatedTo(ChronoUnit.DAYS);
    }


    public ZonedDateTime addOneDay(ZonedDateTime inputDay) {
        return inputDay.plusDays(ONE_DAY);
    }

    public ZonedDateTime addOneHour(ZonedDateTime inputDay) {
        return inputDay.plusHours(ONE_HOUR);
    }

    public ZonedDateTime addOneDayByInputDay(ZonedDateTime inputDay) {
        return inputDay.plusDays(ONE_DAY);
    }


    public boolean isSameDay(ZonedDateTime inputDay) {
        ZonedDateTime today = getTodayTruncatedToDay();
        return today.equals(inputDay);
    }


    public ZonedDateTime getTodayTruncatedToHour() {
        return provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.HOURS);
    }

    public ZonedDateTime getTodayTruncatedToDay() {
        log.info("4th calling getTodayTruncatedToDay");
        ZonedDateTime testValue = provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.DAYS);
        log.info("5th calling getTodayTruncatedToDay value:{}", testValue);
//        return provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.DAYS);
        return testValue;
    }


    public ZonedDateTime daysAgo(Integer hourToSubtract) {
        log.info("calling getTodayTruncatedToDay");
        return provider.getCurrentZonedDateTimeAt().minusDays(hourToSubtract);
    }

    public ZonedDateTime getSearchDay(Integer daysToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        ZonedDateTime yesterday = todayTruncated.minusDays(daysToSubtract);
        return yesterday;
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
