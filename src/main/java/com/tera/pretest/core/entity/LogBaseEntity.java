package com.tera.pretest.core.entity;

import com.tera.pretest.core.config.ZonedDateTimeToStringConvert;
import com.tera.pretest.core.util.TimeProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@ToString
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class LogBaseEntity extends BaseEntity{

    @Convert(converter = ZonedDateTimeToStringConvert.class)
    @Schema(description = "Time Zone Log")
    @Column( nullable = false)
    private ZonedDateTime timeZoneAt;

    @PrePersist
    private void insertLogData(){
        super.insertBaseData();
        if(this.timeZoneAt == null)
            this.timeZoneAt = TimeProvider.getInstance().getCurrentZonedDateTimeAt();

    }

}
