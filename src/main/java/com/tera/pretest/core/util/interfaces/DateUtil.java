package com.tera.pretest.core.util.interfaces;

import java.time.ZonedDateTime;

public interface DateUtil {

    ZonedDateTime truncateZonedDateTimeToHour(ZonedDateTime choiceDayAndHour);

    ZonedDateTime truncateZonedDateTimeToDay(ZonedDateTime choiceDay);

    ZonedDateTime addOneDay(ZonedDateTime inputDay);

    ZonedDateTime addOneHour(ZonedDateTime inputDay);

    ZonedDateTime addOneDayByInputDay(ZonedDateTime inputDay);

    boolean isSameDay(ZonedDateTime inputDay);

    ZonedDateTime getTodayTruncatedToHour();

    ZonedDateTime getTodayTruncatedToDay();

    ZonedDateTime daysAgo(Integer hourToSubtract);

    ZonedDateTime getSearchDay(Integer daysToSubtract);

    ZonedDateTime getSearchMonth(Integer monthToSubtract);

    ZonedDateTime getSearchYear(Integer yearToSubtract);

}
