package com.tera.pretest.core.config;

import lombok.extern.log4j.Log4j2;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Log4j2
@Converter(autoApply = true)
public class ZonedDateTimeToStringConvert implements AttributeConverter<ZonedDateTime, String> {

    private DateTimeFormatter dateTimeFormatter;

    public ZonedDateTimeToStringConvert(DateTimeFormatter dateTimeFormatter){
        log.info("5th final ZonedDateTimeToStringConvert calling dateTimeFormatter:{}",dateTimeFormatter);
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convertToDatabaseColumn(ZonedDateTime inputData) {
        return inputData != null ? inputData.format(dateTimeFormatter) : null;
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String outputData) {
        return outputData != null ? ZonedDateTime.parse(outputData, dateTimeFormatter) : null;
    }

}
