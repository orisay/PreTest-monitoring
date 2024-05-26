package com.tera.pretest.context.cpumonitoring.dto.output;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import lombok.*;

import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultCpuUsageRateByHour {
    private List<CpuUsageRateByHour> statsUsage;

    public static ResultCpuUsageRateByHour toBuild(List<CpuUsageRateByHour> responseData) {
        return ResultCpuUsageRateByHour.builder()
                .statsUsage(responseData)
                .build();
    }
}
