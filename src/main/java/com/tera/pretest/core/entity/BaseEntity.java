package com.tera.pretest.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

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
    @CreatedDate
    @Column(updatable = false)
    private Timestamp createTime;


    @Schema(description = "소프트 딜리트 될 때 값 변경")
    @UpdateTimestamp
    private Timestamp  updateTIme;


}
