package com.tera.pretest.core.entity;

import com.tera.pretest.core.config.ZoneDateTimeToStringConvert;
import com.tera.pretest.core.config.ZoneDateTimeToTimestampConvert;
import com.tera.pretest.core.util.TimeProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class LogBaseEntity extends BaseEntity{

    @Convert(converter = ZoneDateTimeToStringConvert.class)
    @Schema(description = "Time Zone Log")
    @Column( nullable = false)
    private String timeZoneAt;

    @PrePersist
    private void insertLogData(){
        super.insertBaseData();
        if(this.timeZoneAt == null)
            this.timeZoneAt = TimeProvider.getInstance().getCurrentZonedDateTimeAt().getZone().toString();

    }

}
