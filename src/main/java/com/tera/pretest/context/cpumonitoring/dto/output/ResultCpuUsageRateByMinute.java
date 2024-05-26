package com.tera.pretest.context.cpumonitoring.dto.output;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import lombok.*;

import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultCpuUsageRateByMinute {
    private List<CpuUsageRateByMinute> statsUsage;

    public static ResultCpuUsageRateByMinute toBuild(List<CpuUsageRateByMinute> responseData) {
        return ResultCpuUsageRateByMinute.builder()
                .statsUsage(responseData)
                .build();
    }
}
