package com.tera.pretest;

import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.manager.ShutdownManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
@DisplayName("H2 연결 테스트")
@SpringBootTest
@ActiveProfiles("h2Connect")
@ComponentScan(basePackages = "com.tera.pretest")
@EnableJpaRepositories(basePackages = {
        "com.tera.pretest.context.cpumonitoring.repository.base",
        "com.tera.pretest.context.cpumonitoring.repository.backup"
})
public class h2ConnectTest {

    @Autowired
    private CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Autowired
    private BuildFactory buildFactory;

    @Autowired
    private ShutdownManager shutdownManager;

    @Test
    public void testH2Connect1(){
        CpuUsageRateByMinute cpuUsageRateByMinute = buildFactory.toBuildByCpuUsageRateByMinute(1.52);
        log.debug("testH2Connect1 cpuUsageRateByMinute:::::::{}",cpuUsageRateByMinute);
        cpuUsageRateByMinuteRepository.save(cpuUsageRateByMinute);
        Optional<CpuUsageRateByMinute> testEntity = cpuUsageRateByMinuteRepository.findById(cpuUsageRateByMinute.getCpuRateByMinuteSeq());
        testEntity.ifPresent(resultTest -> {
            log.debug("H2 Connect and Save test: {}", resultTest);
        });
        shutdownManager.shutdown();
        assertThat(testEntity.get().getUsageRate()).isEqualTo(1.52);

    }

}
