package com.project.settlement_batch.config;

import com.project.settlement_batch.tasklet.DynamicDateTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
@Deprecated
@RequiredArgsConstructor
public class DynamicDateBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DynamicDateTasklet dateTasklet;

    @Bean
    public Job dynamicDateJob() {
        return new JobBuilder("dynamicDateJob", jobRepository)
                .start(dynamicDateStep())
                .build();
    }

    @Bean
    public Step dynamicDateStep() {
        return new StepBuilder("dynamicDateStep", jobRepository)
                .tasklet(dateTasklet, transactionManager)
                .build();
    }
}

