package com.project.settlement_batch.config;

import com.project.settlement_batch.tasklet.FileCleanupTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class FileBatchConfig {

    private final Clock clock;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean(name = "fileCleanupTasklet")
    public Tasklet fileCleanupTasklet() {
        return new FileCleanupTasklet(clock, "./test-logs", 30);
    }

    @Bean(name = "fileCleanupJob")
    public Job fileCleanupJob() {
        return new JobBuilder("fileCleanupJob", jobRepository)
                .start(fileCleanupStep())
                .build();
    }

    @Bean(name = "fileCleanupStep")
    public Step fileCleanupStep() {
        return new StepBuilder("fileCleanupStep", jobRepository)
                .tasklet(fileCleanupTasklet(), transactionManager)
                .build();
    }
}

