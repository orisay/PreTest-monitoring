package com.tera.pretest.core.entity;

import com.tera.pretest.core.config.TimeProviderListener;
import com.tera.pretest.core.config.ZonedDateTimeToTimestampConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Log4j2
@ToString
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(TimeProviderListener.class)
public class BaseEntity {

    @Schema(description = "Soft Delete Backup Flag")
    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String flag;

    @Convert(converter = ZonedDateTimeToTimestampConvert.class)
    @Schema(description = "저장된 시간")
    @Column(updatable = false)
    private ZonedDateTime createTime;

    @Convert(converter = ZonedDateTimeToTimestampConvert.class)
    @Schema(description = "소프트 딜리트 될 때 값 변경")
    @LastModifiedDate
    private ZonedDateTime updateTime;

    @PrePersist
    protected void insertBaseData() {
        log.debug("Calling insertBaseData");
        if (this.flag == null)
            flag = "N";
        if(this.createTime == null)
            createTime= TimeProviderListener.getCurrentZonedDateTime();
    }

}
