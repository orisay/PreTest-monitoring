package com.tera.pretest.core.util;

import com.tera.pretest.core.util.interfaces.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.tera.pretest.core.constant.MonitoringConstant.ONE_DAY;
import static com.tera.pretest.core.constant.MonitoringConstant.ONE_HOUR;

@Log4j2
@Component
public class ProviderDateUtil implements DateUtil {

    public static TimeProvider provider;

    @Autowired
    public ProviderDateUtil(TimeProvider provider) {
        this.provider = provider;
    }

    @Override
    public ZonedDateTime truncateZonedDateTimeToHour(ZonedDateTime choiceDayAndHour) {
        return choiceDayAndHour.truncatedTo(ChronoUnit.HOURS);
    }

    @Override
    public ZonedDateTime truncateZonedDateTimeToDay(ZonedDateTime choiceDay) {
        return choiceDay.truncatedTo(ChronoUnit.DAYS);
    }

    @Override
    public ZonedDateTime addOneDay(ZonedDateTime inputDay) {
        return inputDay.plusDays(ONE_DAY);
    }

    @Override
    public ZonedDateTime addOneHour(ZonedDateTime inputDay) {
        return inputDay.plusHours(ONE_HOUR);
    }

    @Override
    public ZonedDateTime addOneDayByInputDay(ZonedDateTime inputDay) {
        return inputDay.plusDays(ONE_DAY);
    }

    @Override
    public boolean isSameDay(ZonedDateTime inputDay) {
        ZonedDateTime today = getTodayTruncatedToDay();
        return today.equals(inputDay);
    }

    @Override
    public ZonedDateTime getTodayTruncatedToHour() {
        return provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.HOURS);
    }

    @Override
    public ZonedDateTime getTodayTruncatedToDay() {
        return provider.getCurrentZonedDateTimeAt().truncatedTo(ChronoUnit.DAYS);
    }

    @Override
    public ZonedDateTime daysAgo(Integer hourToSubtract) {
        return provider.getCurrentZonedDateTimeAt().minusDays(hourToSubtract);
    }

    @Override
    public ZonedDateTime getSearchDay(Integer daysToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        return todayTruncated.minusDays(daysToSubtract);
    }

    @Override
    public ZonedDateTime getSearchMonth(Integer monthToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        return todayTruncated.minusMonths(monthToSubtract);
    }

    @Override
    public ZonedDateTime getSearchYear(Integer yearToSubtract) {
        ZonedDateTime todayTruncated = getTodayTruncatedToDay();
        return todayTruncated.minusDays(yearToSubtract);
    }

}
