package com.project.settlement_batch.listener;

import com.project.settlement_batch.vo.DateTimeRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobLoggerListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("=================================");
        log.info("'{}' 배치를 시작합니다!", jobExecution.getJobInstance().getJobName());
        log.info("=================================");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        DateTimeRange runTime = DateTimeRange.of(jobExecution.getStartTime(), jobExecution.getEndTime());
        log.info("=================================");
        log.info("'{}' 배치가 종료되었습니다. (상태 : {})", jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
        log.info("총 소요 시간: {} ms", runTime.duration().toMillis());
        log.info("=================================");

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("배치가 실패했습니다!");
        }
    }
}
