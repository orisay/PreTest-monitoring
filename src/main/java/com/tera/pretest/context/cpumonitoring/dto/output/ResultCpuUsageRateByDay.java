package com.tera.pretest.context.cpumonitoring.dto.output;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import lombok.*;

import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultCpuUsageRateByDay {
    private List<CpuUsageRateByDay> statsUsage;

    public static ResultCpuUsageRateByDay toBuild(List<CpuUsageRateByDay> responseData){
        return ResultCpuUsageRateByDay.builder()
                .statsUsage(responseData)
                .build();

    }
}
