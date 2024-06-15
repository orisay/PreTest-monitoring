package com.tera.pretest.core.config;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.tera.pretest.core.constant.MonitoringConstant.TIME_ZONE;


@Log4j2
@AllArgsConstructor
@Converter(autoApply = false)
@Component
public class ZonedDateTimeToTimestampConvert implements AttributeConverter<ZonedDateTime, Timestamp> {

    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime inputData) {
        log.info("Calling convertToDatabaseColumn");
        return inputData != null ? Timestamp.from(inputData.toInstant()) : null;
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp outputData) {
        log.info("Calling convertToEntityAttribute");
        return outputData !=null ? ZonedDateTime.ofInstant(outputData.toInstant(), ZoneId.of(TIME_ZONE)) :  null;
    }
}
