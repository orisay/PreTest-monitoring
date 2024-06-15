package com.tera.pretest.cpumonitoring.service.total;

import com.tera.pretest.config.IntegratedTestConfig;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByDayBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByHourBackup;
import com.tera.pretest.context.cpumonitoring.entity.backup.CpuUsageRateByMinuteBackup;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByDay;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByHour;
import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.factory.BuildFactory;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByDayBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByHourBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.backup.CpuUsageRateByMinuteBackupRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByDayRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByHourRepository;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import com.tera.pretest.core.exception.process.ProcessCustomException;
import com.tera.pretest.core.monitoring.service.CpuMonitoringBackupService;
import com.tera.pretest.core.util.TimeProvider;
import com.tera.pretest.cpumonitoring.core.helper.DaySetupHelper;
import com.tera.pretest.cpumonitoring.core.helper.HourSetupHelper;
import com.tera.pretest.cpumonitoring.core.helper.MinuteSetupHelper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Clock;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.tera.pretest.core.constant.MonitoringConstant.DELETE_FLAG;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@SpringBootTest
@ContextConfiguration(classes = IntegratedTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@ActiveProfiles("totalTest")
@DisplayName("BackupServiceTests")
public class CpuMonitoringBackupServiceIntegratedTests {

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;
    private final CpuUsageRateByHourRepository cpuUsageRateByHourRepository;
    private final CpuUsageRateByDayRepository cpuUsageRateByDayRepository;
    private final CpuUsageRateByMinuteBackupRepository cpuUsageRateByMinuteBackupRepository;
    private final CpuUsageRateByHourBackupRepository cpuUsageRateByHourBackupRepository;
    private final CpuUsageRateByDayBackupRepository cpuUsageRateByDayBackupRepository;
    private final BuildFactory buildFactory;
    private final CpuMonitoringBackupService cpuMonitoringBackupService;
    private final MinuteSetupHelper minuteSetupHelper;
    private final HourSetupHelper hourSetupHelper;
    private final DaySetupHelper daySetupHelper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    @Qualifier("basicClock")
    private Clock baseClock;

    @Autowired
    @Qualifier("FixedTestClock")
    private Clock testClock;

    @Autowired
    public CpuMonitoringBackupServiceIntegratedTests(CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository
            , CpuUsageRateByHourRepository cpuUsageRateByHourRepository
            , CpuUsageRateByDayRepository cpuUsageRateByDayRepository
            , CpuUsageRateByMinuteBackupRepository cpuUsageRateByMinuteBackupRepository
            , CpuUsageRateByHourBackupRepository cpuUsageRateByHourBackupRepository
            , CpuUsageRateByDayBackupRepository cpuUsageRateByDayBackupRepository
            , BuildFactory buildFactory, CpuMonitoringBackupService cpuMonitoringBackupService
            , MinuteSetupHelper minuteSetupHelper, HourSetupHelper hourSetupHelper, DaySetupHelper daySetupHelper
    ) {
        this.cpuUsageRateByMinuteRepository = cpuUsageRateByMinuteRepository;
        this.cpuUsageRateByHourRepository = cpuUsageRateByHourRepository;
        this.cpuUsageRateByDayRepository = cpuUsageRateByDayRepository;
        this.cpuUsageRateByMinuteBackupRepository = cpuUsageRateByMinuteBackupRepository;
        this.cpuUsageRateByHourBackupRepository = cpuUsageRateByHourBackupRepository;
        this.cpuUsageRateByDayBackupRepository = cpuUsageRateByDayBackupRepository;
        this.buildFactory = buildFactory;
        this.cpuMonitoringBackupService = cpuMonitoringBackupService;
        this.minuteSetupHelper = minuteSetupHelper;
        this.hourSetupHelper = hourSetupHelper;
        this.daySetupHelper = daySetupHelper;
    }

    @BeforeEach
    public void setupCommon() {
        log.debug("Calling setupCommon");
        timeProvider.setClockFixedTime(testClock);
    }


    @AfterEach
    public void shutUpCommon() {
        log.debug("Calling shutUpCommon");
        timeProvider.setClockFixedTime(baseClock);
        entityManager.clear();
    }

    @Nested
    @Transactional
    @DisplayName("분 단위 데이터 백업")
    class BackupCpuUsageStatsByMinute {

        private List<CpuUsageRateByMinute> oldData;

        private List<CpuUsageRateByMinuteBackup> resultData;


        @BeforeEach
        public void setup() {
            minuteSetupHelper.setup();
            minuteSetupHelper.requestTempData();
            minuteSetupHelper.setFlagForDeletionAndBackup();
            testDataSettingProcessForBackup();
        }

        @AfterEach
        public void shutUp() {
            entityManager.clear();
        }

        @Test
        @DisplayName("백업 전 데이터 설정")
        @Transactional
        public void testDataSettingProcessForBackup() {
            CpuUsageRateByMinute firstDbData = minuteSetupHelper.getFirstDbData();
            CpuUsageRateByMinute secondDbData = minuteSetupHelper.getSecondDbData();
            oldData = Arrays.asList(firstDbData, secondDbData);
            List<CpuUsageRateByMinuteBackup> backupData = buildFactory.toBackupDataByMinuteStats(oldData);
            resultData = cpuUsageRateByMinuteBackupRepository.saveAll(backupData);
        }

        @Test
        @DisplayName("리스트 값 존재")
        public void testResultNotEmpty() {
            assertThat(resultData).isNotEmpty();
        }

        @Test
        @DisplayName("리스트 값 예상값 검증")
        public void conditionListValue() {
            assertThat(resultData.get(0).getCpuRateByMinuteSeq()).isEqualTo(1L);
        }

        @Test
        @DisplayName("소프트 딜리트 확인")
        public void duplicateBackupData() {
            assertThat(oldData.get(0)).isNotNull()
                    .usingRecursiveComparison().ignoringFields("createTime", "updateTime", "timeZoneAt")
                    .isEqualTo(resultData.get(0));
        }

        @Test
        @DisplayName("조건문 예외 처리 - backupData isEmpty")
        public void backupValueIsEmpty() {
            log.debug("Calling backupValueIsEmpty backupData init before Value:{}", oldData);
            oldData = Collections.emptyList();
            log.debug("Calling backupValueIsEmpty oldData init after Value:{}", oldData);

            Future<Void> result = cpuMonitoringBackupService.backupCpuUsageStatsByMinute(oldData);
            ExecutionException exception = assertThrows(ExecutionException.class, () -> result.get());

            Throwable cause = exception.getCause();
            log.debug("exception.getClass :{}",cause.getClass());
            log.info("Error message that occurred :{}", cause.getMessage());
            assertThat(cause.getClass()).isEqualTo(ProcessCustomException.class);

        }


    }

    @Nested
    @Transactional
    @DisplayName("시 단위 데이터 백업")
    class BackupCpuUsageStatsByHour {

        private List<CpuUsageRateByHour> oldData;

        private List<CpuUsageRateByHourBackup> resultData;

        @BeforeEach
        public void setup() {
            hourSetupHelper.setup();
            hourSetupHelper.requestTempData();
            hourSetupHelper.setFlagForDeletionAndBackup();
            testDataSettingProcessForBackup();
        }

        @AfterEach
        public void shutUp() {
            entityManager.clear();
        }


        @Test
        @DisplayName("백업 전 데이터 설정")
        @Transactional
        public void testDataSettingProcessForBackup() {
            CpuUsageRateByHour firstDbData = hourSetupHelper.getFirstDbData();
            CpuUsageRateByHour secondDbData = hourSetupHelper.getSecondDbData();
            oldData = Arrays.asList(firstDbData, secondDbData);
            List<CpuUsageRateByHourBackup> backupData = buildFactory.toBackupDataByHourStats(oldData);
            resultData = cpuUsageRateByHourBackupRepository.saveAll(backupData);
        }

        @Test
        @DisplayName("리스트 값 존재")
        public void testResultNotEmpty() {
            assertThat(resultData).isNotEmpty();
        }

        @Test
        @DisplayName("리스트 값 예상값 검증")
        public void conditionListValue() {
            assertThat(resultData.get(0).getMaximumUsage()).isEqualTo(oldData.get(0).getMaximumUsage());
        }

        @Test
        @DisplayName("소프트 딜리트 확인")
        public void duplicateBackupData() {
            log.info("Check oldData Soft Delete value:{}", oldData.get(0).getFlag());
            assertThat(oldData.get(0)).isNotNull()
                    .usingRecursiveComparison().ignoringFields("createTime", "updateTime", "timeZoneAt")
                    .isEqualTo(resultData.get(0));
        }

        @Test
        @DisplayName("조건문 예외 처리 - backupData isEmpty")
        public void backupValueIsEmpty() {
            log.debug("Calling backupValueIsEmpty backupData init before Value:{}", oldData);
            oldData = Collections.emptyList();
            log.debug("Calling backupValueIsEmpty oldData init after Value:{}", oldData);

            Future<Void> result = cpuMonitoringBackupService.backupCpuUsageStatsByHour(oldData);
            ExecutionException exception = assertThrows(ExecutionException.class, () -> result.get());

            Throwable cause = exception.getCause();
            log.debug("exception.getClass :{}",cause.getClass());
            log.info("Error message that occurred :{}", cause.getMessage());
            assertThat(cause.getClass()).isEqualTo(ProcessCustomException.class);
        }

    }

    @Nested
    @DisplayName("일 단위 데이터 백업")
    class BackupCpuUsageStatsByDay {
        private List<CpuUsageRateByDay> oldData;

        private List<CpuUsageRateByDayBackup> resultData;

        @BeforeEach
        public void setup() {
            daySetupHelper.setup();
            daySetupHelper.requestTempData();
            daySetupHelper.setFlagForDeletionAndBackup();
            testDataSettingProcessForBackup();
        }

        @AfterEach
        public void shutUp() {
            entityManager.clear();
        }

        @Test
        @DisplayName("백업 전 데이터 설정")
        @Transactional
        public void testDataSettingProcessForBackup() {
            CpuUsageRateByDay firstDbData = daySetupHelper.getFirstDbData();
            CpuUsageRateByDay secondDbData = daySetupHelper.getSecondDbData();
            oldData = Arrays.asList(firstDbData, secondDbData);
            List<CpuUsageRateByDayBackup> backupData = buildFactory.toBackupDataByDayStats(oldData);
            resultData = cpuUsageRateByDayBackupRepository.saveAll(backupData);
        }

        @Test
        @DisplayName("리스트 값 존재")
        public void testResultNotEmpty() {
            assertThat(resultData).isNotEmpty();
        }

        @Test
        @DisplayName("리스트 값 예상값 검증")
        public void conditionListValue() {
            assertThat(resultData.get(0).getMaximumUsage()).isEqualTo(75.00);
        }

        @Test
        @DisplayName("소프트 딜리트 확인")
        public void duplicateBackupData() {
            assertThat(oldData.get(0)).isNotNull()
                    .usingRecursiveComparison().ignoringFields("createTime", "updateTime", "timeZoneAt")
                    .isEqualTo(resultData.get(0));
        }

        @Test
        @DisplayName("조건문 예외 처리 - backupData isEmpty")
        public void backupValueIsEmpty() {
            log.debug("Calling backupValueIsEmpty backupData init before Value:{}", oldData);
            oldData = Collections.emptyList();
            log.debug("Calling backupValueIsEmpty oldData init after Value:{}", oldData);

            Future<Void> result = cpuMonitoringBackupService.backupCpuUsageStatsByDay(oldData);
            ExecutionException exception = assertThrows(ExecutionException.class, () -> result.get());

            Throwable cause = exception.getCause();
            log.debug("exception.getClass :{}",cause.getClass());
            log.info("Error message that occurred :{}", cause.getMessage());
            assertThat(cause.getClass()).isEqualTo(ProcessCustomException.class);
        }

    }

    @Nested
    @Transactional
    @DisplayName("분 단위 데이터 하드 딜리트")
    class HardDeleteOutdatedCpuUsageStatsByMinute {

        private final long EXPECT_HARD_DELETE_COUNT = 2L;
        private long hardDeleteEffectRecode;

        @BeforeEach
        public void setup() {
            minuteSetupHelper.setup();
            minuteSetupHelper.setFlagForDeletionAndBackup();
            minuteSetupHelper.saveDbData();
            testDataSettingProcessForHardDelete();
            entityManager.flush();
        }

        @Test
        @DisplayName("하드 딜리트 전 데이터 설정")
        @Transactional
        public void testDataSettingProcessForHardDelete() {
            List<CpuUsageRateByMinute> checkData = cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG);
            log.debug("testDataSettingProcessForHardDelete CheckData:{}", checkData);
            hardDeleteEffectRecode = cpuUsageRateByMinuteRepository.deleteByFlag(DELETE_FLAG);
        }

        @AfterEach
        public void shutUp() {
            log.info("Calling shutUp");
            cpuUsageRateByMinuteRepository.deleteAll();
            entityManager.clear();
        }

        @Test
        @DisplayName("DB 기존 테이블 검사")
        public void checkSoftDeleteDbRecode() {
            List<CpuUsageRateByMinute> emptyResult = cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG);
            assertThat(emptyResult).isEmpty();
        }

        @Test
        @DisplayName("예상 삭제 레코드 갯수 검사")
        public void duplicateHardDeleteEffectRecordCount() {
            List<CpuUsageRateByMinute> checkData = cpuUsageRateByMinuteRepository.findByFlag(DELETE_FLAG);
            log.debug("duplicateHardDeleteEffectRecordCount CheckData:{}", checkData);
            log.info("duplicateHardDeleteEffectRecordCount hardDeleteEffectRecodeCount:{}", hardDeleteEffectRecode);
            assertThat(hardDeleteEffectRecode).isEqualTo(EXPECT_HARD_DELETE_COUNT);
        }

    }

    @Nested
    @Transactional
    @DisplayName("시 단위 데이터 하드 딜리트")
    class hardDeleteOutdatedCpuUsageStatsByHour {

        private final long EXPECT_HARD_DELETE_COUNT = 2L;
        private long hardDeleteEffectRecode;

        @BeforeEach
        public void setup() {
            hourSetupHelper.setup();
            hourSetupHelper.setFlagForDeletionAndBackup();
            hourSetupHelper.saveDbData();
            testDataSettingProcessForHardDelete();
            entityManager.flush();
        }

        @Test
        @DisplayName("하드 딜리트 전 데이터 설정")
        @Transactional
        public void testDataSettingProcessForHardDelete() {
            List<CpuUsageRateByHour> checkData = cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG);
            log.debug("testDataSettingProcessForHardDelete CheckData:{}", checkData);
            hardDeleteEffectRecode = cpuUsageRateByHourRepository.deleteByFlag(DELETE_FLAG);
        }

        @AfterEach
        public void shutUp() {
            log.info("Calling shutUp");
            cpuUsageRateByHourBackupRepository.deleteAll();
        }

        @Test
        @DisplayName("DB 기존 테이블 검사")
        public void checkSoftDeleteDbRecode() {
            List<CpuUsageRateByHour> emptyResult = cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG);
            assertThat(emptyResult).isEmpty();
        }

        @Test
        @DisplayName("예상 삭제 레코드 갯수 검사")
        public void duplicateHardDeleteEffectRecordCount() {
            List<CpuUsageRateByHour> checkData = cpuUsageRateByHourRepository.findByFlag(DELETE_FLAG);
            log.debug("duplicateHardDeleteEffectRecordCount CheckData:{}", checkData);
            log.info("duplicateHardDeleteEffectRecordCount hardDeleteEffectRecodeCount:{}", hardDeleteEffectRecode);
            assertThat(hardDeleteEffectRecode).isEqualTo(EXPECT_HARD_DELETE_COUNT);
        }

    }

    @Nested
    @Transactional
    @DisplayName("일 단위 데이터 하드 딜리트")
    class hardDeleteOutdatedCpuUsageStatsByDay {
        private final long EXPECT_HARD_DELETE_COUNT = 2L;
        private long hardDeleteEffectRecode;

        @BeforeEach
        public void setup() {
            daySetupHelper.setup();
            daySetupHelper.setFlagForDeletionAndBackup();
            daySetupHelper.saveDbData();
            testDataSettingProcessForHardDelete();
            entityManager.flush();
        }

        @Test
        @DisplayName("하드 딜리트 전 데이터 설정")
        @Transactional
        public void testDataSettingProcessForHardDelete() {
            List<CpuUsageRateByDay> checkData = cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG);
            log.debug("testDataSettingProcessForHardDelete CheckData:{}", checkData);
            hardDeleteEffectRecode = cpuUsageRateByDayRepository.deleteByFlag(DELETE_FLAG);
        }

        @AfterEach
        public void shutUp() {
            cpuUsageRateByDayRepository.deleteAll();
        }

        @Test
        @DisplayName("DB 기존 테이블 검사")
        public void checkSoftDeleteDbRecode() {
            List<CpuUsageRateByDay> emptyResult = cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG);
            assertThat(emptyResult).isEmpty();
        }

        @Test
        @DisplayName("예상 삭제 레코드 갯수 검사")
        public void duplicateHardDeleteEffectRecordCount() {
            List<CpuUsageRateByDay> checkData = cpuUsageRateByDayRepository.findByFlag(DELETE_FLAG);
            log.debug("duplicateHardDeleteEffectRecordCount CheckData:{}", checkData);
            log.info("duplicateHardDeleteEffectRecordCount hardDeleteEffectRecodeCount:{}", hardDeleteEffectRecode);
            assertThat(hardDeleteEffectRecode).isEqualTo(EXPECT_HARD_DELETE_COUNT);
        }

    }

}
