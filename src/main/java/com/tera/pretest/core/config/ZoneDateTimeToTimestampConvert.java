package com.tera.pretest.core.config;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.tera.pretest.core.contant.MonitoringConstant.TIME_ZONE;


@Converter(autoApply = true)
public class ZoneDateTimeToTimestampConvert implements AttributeConverter<ZonedDateTime, Timestamp> {

    private static final DateTimeFormatter dateTimeFormatter = ZoneDateTimeFormatConfig.dateTimeFormatter;

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime inputData) {
        return inputData != null ? Timestamp.from(inputData.toInstant()) : null;
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp outputData) {
        return outputData !=null ? ZonedDateTime.ofInstant(outputData.toInstant(), ZoneId.of(TIME_ZONE)) :  null;
    }
}
