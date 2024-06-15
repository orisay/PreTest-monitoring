package com.tera.pretest.core.config;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Log4j2
@AllArgsConstructor
@Converter(autoApply = false)
@Component
public class ZonedDateTimeToStringConvert implements AttributeConverter<ZonedDateTime, String> {

    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public String convertToDatabaseColumn(ZonedDateTime inputData) {
        log.debug("Calling convertToDatabaseColumn");
        return inputData != null ? inputData.format(dateTimeFormatter) : null;
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String outputData) {
        log.debug("Calling convertToEntityAttribute");
        return outputData != null ? ZonedDateTime.parse(outputData, dateTimeFormatter) : null;
    }

}
