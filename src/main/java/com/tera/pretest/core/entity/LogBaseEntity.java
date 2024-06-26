package com.tera.pretest.core.entity;

import com.tera.pretest.core.config.TimeProviderListener;
import com.tera.pretest.core.config.ZonedDateTimeToStringConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Log4j2
@ToString(callSuper = true)
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(TimeProviderListener.class)
public class LogBaseEntity extends BaseEntity {

    @Convert(converter = ZonedDateTimeToStringConvert.class)
    @Schema(description = "Time Zone Log")
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private ZonedDateTime timeZoneAt;


    @PrePersist
    private void insertLogData() {
        super.insertBaseData();
        log.info("insertLogData before nullCheck timeZoneAt:{}", timeZoneAt);
        if (this.timeZoneAt == null)
            this.timeZoneAt = TimeProviderListener.getCurrentZonedDateTime();
        log.info("insertLogData after nullCheck timeZoneAt:{}", timeZoneAt);

    }

}
