package com.tera.pretest.core.config;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Converter(autoApply = true)
public class ZoneDateTimeToStringConvert implements AttributeConverter<ZonedDateTime, String> {

    private static final DateTimeFormatter dateTimeFormatter = ZoneDateTimeFormatConfig.dateTimeFormatter;

    @Override
    public String convertToDatabaseColumn(ZonedDateTime inputData) {
        return inputData != null ? inputData.format(dateTimeFormatter) : null;
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String outputData) {
        return outputData != null ? ZonedDateTime.parse(outputData, dateTimeFormatter) : null;
    }

}
