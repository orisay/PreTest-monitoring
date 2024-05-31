package com.tera.pretest.context.cpumonitoring.dto.input;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCpuUsageRateByHour {

    @NotNull(message = "조회를 원하는 날짜가 입력되지 않았습니다.")
    private ZonedDateTime startDay;
}
