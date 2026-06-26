package com.project.settlement_batch.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementScheduler {

    private final Clock clock;
    private final JobOperator jobOperator;
    private final Job settlementJob;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetDate", LocalDate.now(clock).minusDays(7).toString())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            log.info("스케줄러 작동! 배치를 실행합니다.");
            jobOperator.start(settlementJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("배치 실행 중 예외가 발생했습니다.", e);
        } catch (InvalidJobParametersException e) {
            log.error("배치 실행 중 예외가 발생했습니다.", e);
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("배치 실행 중 예외가 발생했습니다.", e);
        } catch (JobRestartException e) {
            log.error("배치 실행 중 예외가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("알 수 없는 에러 발생");
        }
    }

}
