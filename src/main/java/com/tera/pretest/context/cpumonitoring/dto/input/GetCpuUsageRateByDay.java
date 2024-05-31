package com.tera.pretest.context.cpumonitoring.dto.input;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCpuUsageRateByDay {

    @NotNull(message = "조회 시작 일이 입력되지 않았습니다.")
    @PastOrPresent(message = "조회 시작 일이 조회 종료 일 보다 미래일 수 없습니다.")
    private ZonedDateTime startDay;

    @NotNull(message = "조회 종료 일이 입력되지 않았습니다.")
    @FutureOrPresent(message = "조회 종료 일이 조회 시작 일보다 과거일 수 없습니다.")
    private ZonedDateTime endDay;
}
