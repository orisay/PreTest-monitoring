package com.tera.pretest;

import com.tera.pretest.config.UnitTestConfig;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.config.ZonedDateTimeFormatConfig;
import com.tera.pretest.core.manager.ShutdownManager;
import com.tera.pretest.core.util.DateUtil;
import com.tera.pretest.core.util.TimeProvider;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.tera.pretest.core.contant.MonitoringConstant.TIME_ZONE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Log4j2
@DisplayName("H2 연결 테스트")
@SpringBootTest(classes = {ZonedDateTimeFormatConfig.class, UnitTestConfig.class})
@ActiveProfiles("h2Connect")
@ComponentScan(basePackages = "com.tera.pretest")
@EnableJpaRepositories(basePackages = {
        "com.tera.pretest.context.cpumonitoring.repository.base",
        "com.tera.pretest.context.cpumonitoring.repository.backup"
})
public class h2ConnectTest {

    @Autowired
    private CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    @Test
    public void testH2Connect1(){
        DateTimeFormatter formatter = ZonedDateTimeFormatConfig.dateTimeFormatter;
        CpuUsageRateByMinute cpuUsageRateByMinute = CpuUsageRateByMinute.builder()
                .usageRate(1.52)
                .createTime(Timestamp.from(ZonedDateTime.now().toInstant()))
                .timeZoneAt(ZonedDateTime.now(ZoneId.of(TIME_ZONE)))
                .build();

        log.info("testH2Connect1 cpuUsageRateByMinute:::::::{}",cpuUsageRateByMinute);
        cpuUsageRateByMinuteRepository.save(cpuUsageRateByMinute);
        Optional<CpuUsageRateByMinute> testEntity = cpuUsageRateByMinuteRepository.findById(cpuUsageRateByMinute.getCpuRateByMinuteSeq());
        testEntity.ifPresent(resultTest -> {
            log.debug("H2 Connect and Save test: {}", resultTest);
        });
        assertThat(testEntity.get().getUsageRate()).isEqualTo(1.52);

    }

}
