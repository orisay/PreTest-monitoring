package com.tera.pretest;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@Log4j2
@DisplayName("H2 연결 테스트")
@DataJpaTest
@ActiveProfiles("h2Connect")
public class h2ConnectTest {
    @Autowired
    private CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Test
    public void testH2Connect1(){
        CpuUsageRateByMinute cpuUsageRateByMinute = new CpuUsageRateByMinute();
        cpuUsageRateByMinute.setUsage(1.52);
        cpuUsageRateByMinuteRepository.save(cpuUsageRateByMinute);
        Optional<CpuUsageRateByMinute> testEntity = cpuUsageRateByMinuteRepository.findById(cpuUsageRateByMinute.getCpuRateByMinuteSeq());
        testEntity.ifPresent(resultTest -> {
            log.debug("H2 Connect and Save test: {}", resultTest);
        });
        assertThat(testEntity.get().getUsage()).isEqualTo(1.52);

    }

}
