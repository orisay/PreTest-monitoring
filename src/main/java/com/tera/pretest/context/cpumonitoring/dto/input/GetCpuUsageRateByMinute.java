package com.tera.pretest.context.cpumonitoring.dto.input;

import lombok.*;

import java.sql.Timestamp;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCpuUsageRateByMinute {
    private Timestamp startDay;
    private Timestamp endDay;
}
