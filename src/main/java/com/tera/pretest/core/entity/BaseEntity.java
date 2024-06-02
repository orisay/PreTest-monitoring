package com.tera.pretest.core.entity;

import com.tera.pretest.core.util.TimeProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

@ToString
@Getter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
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
            createTime= TimeProvider.getInstance().getCurrentTimestampAt();
    }

}
