package com.tera.pretest.core.entity;

import com.tera.pretest.core.config.TimeProviderListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.sql.Timestamp;

@ToString
@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(TimeProviderListener.class)
public class BaseEntity {

    @Schema(description = "Soft Delete Backup Flag")
    @Column(insertable = false, nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String flag;

    @Schema(description = "저장된 시간")
    @Column(updatable = false)
    protected Timestamp createTime;

    @Schema(description = "소프트 딜리트 될 때 값 변경")
    @LastModifiedDate
    private Timestamp updateTIme;

    @PrePersist
    protected void insertBaseData() {
        if (this.flag == null)
            flag = "N";
        if(this.createTime == null)
            createTime= TimeProviderListener.getCurrentTimeStamp();
    }

}
