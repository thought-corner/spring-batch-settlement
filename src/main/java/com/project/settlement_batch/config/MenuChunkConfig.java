package com.project.settlement_batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MenuChunkConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job menuJob() {
        return new JobBuilder("menuJob", jobRepository)
                .start(menuStep())
                .build();
    }

    @Bean
    public Step menuStep() {
        return new StepBuilder("menuStep", jobRepository)
                .<String, String>chunk(2)
                .reader(menuReader())
                .processor(menuProcessor())
                .writer(menuWriter())
                .build();
    }

    @Bean
    public ItemReader<String> menuReader() {
        return new ListItemReader<>(Arrays.asList(
                "ice americano", "latte", "mocha", "cappuccino", "espresso"
        ));
    }

    @Bean
    public ItemProcessor<String, String> menuProcessor() {
        return String::toUpperCase;
    }

    @Bean
    public ItemWriter<String> menuWriter() {
        return items -> {
            log.info("-- 청크 쓰기 시작 --");
            for (String item : items) {
                log.info("결과 : {}", item);
            }
            log.info("-- 청크 쓰기 완료 --");
        };
    }
}
