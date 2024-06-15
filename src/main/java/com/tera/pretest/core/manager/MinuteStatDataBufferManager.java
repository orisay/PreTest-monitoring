package com.tera.pretest.core.manager;


import com.tera.pretest.context.cpumonitoring.entity.base.CpuUsageRateByMinute;
import com.tera.pretest.context.cpumonitoring.repository.base.CpuUsageRateByMinuteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.tera.pretest.core.contant.MonitoringConstant.*;

@Log4j2
@Component
public class MinuteStatDataBufferManager {

    private final Queue<CpuUsageRateByMinute> inputData = new ConcurrentLinkedDeque<>();

    private final AtomicInteger queueSize = new AtomicInteger(0);

    private final CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository;

    private final Lock pessimisticLock = new ReentrantLock();


    public MinuteStatDataBufferManager(CpuUsageRateByMinuteRepository cpuUsageRateByMinuteRepository) {
        this.cpuUsageRateByMinuteRepository = cpuUsageRateByMinuteRepository;
    }

    public void collectCpuUsageRateByMinuteData(CpuUsageRateByMinute collectData) {
        pessimisticLock.lock();
        try {
            inputData.add(collectData);
            int currentQueueSize = queueSize.incrementAndGet();
            if (currentQueueSize > LIMIT_DATA_COUNT)
                checkAndInsertCpuUsageRateByMinuteData();
        } finally {
            pessimisticLock.unlock();
        }

    }

    private void checkAndInsertCpuUsageRateByMinuteData() {
        List<CpuUsageRateByMinute> insertData = convertInsertData();
        insertCpuUsageRateByMinuteData(insertData);
    }

    private List<CpuUsageRateByMinute> convertInsertData() {
        List<CpuUsageRateByMinute> insertData = new ArrayList<>();
        while (!inputData.isEmpty() && insertData.size() < LIMIT_DATA_COUNT) {
            CpuUsageRateByMinute data = inputData.poll();
            queueSize.decrementAndGet();
            if (data != null)
                insertData.add(data);
        }
        return insertData;
    }

    @Retryable(value = {DataAccessException.class}, maxAttempts = FINAL_RETRY, backoff = @Backoff(delay = RETRY_DELAY))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void insertCpuUsageRateByMinuteData(List<CpuUsageRateByMinute> insertData) {
        log.info("Calling insertCpuUsageRateByMinuteData insertData:{}", insertData);
        List<CpuUsageRateByMinute> saveData = cpuUsageRateByMinuteRepository.saveAll(insertData);
        log.info("Calling insertCpuUsageRateByMinuteData saveData:{}", saveData);
    }


}
