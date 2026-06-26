package com.project.settlement_batch.config;

import com.project.settlement_batch.domain.Orders;
import com.project.settlement_batch.domain.Settlement;
import com.project.settlement_batch.listener.JobLoggerListener;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SettlementBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final JobLoggerListener jobLoggerListener;

    @Bean(name = "settlementJob")
    public Job settlementJob() {
        return new JobBuilder("settlementJob", jobRepository)
                .listener(jobLoggerListener)
                .start(settlementStep())
                .build();
    }

    @Bean(name = "settlementStep")
    public Step settlementStep() {
        return new StepBuilder("settlementStep", jobRepository)
                .<Orders, Settlement>chunk(1000)
                .transactionManager(transactionManager)
                .reader(settlementItemReader(null))
                .processor(settlementItemProcessor())
                .writer(settlementItemWriter())
                .build();
    }

    /**
     * 가공된 데이터를 메모리 상의 리스트(Chunk)에 차곡차곡 쌓아놓는다.
     */
    @StepScope
    @Bean(name = "settlementItemReader")
    public JpaPagingItemReader<Orders> settlementItemReader(@Value("#{jobParameters['settlementDate']}") String settlementDate) {
        log.info("[Reader] 정산 집계 대상 날짜 : {}", settlementDate);
        return new JpaPagingItemReaderBuilder<Orders>()
                .name("settlementItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(3000) // Chunk Size = 3000
                .queryString("SELECT o FROM Orders o WHERE o.orderDate = :settlementDate ORDER BY o.id")
                .parameterValues(Collections.singletonMap("settlementDate", LocalDate.parse(settlementDate)))
                .build();
    }

    @Bean(name = "settlementItemProcessor")
    public ItemProcessor<Orders, Settlement> settlementItemProcessor() {
        return item -> {
            int fee = (int) (item.getAmount() * 0.03);
            int settlementAmount = item.getAmount() - fee;
            return Settlement.create(item.getId(), item.getStoreName(), settlementAmount, LocalDate.now());
        };
    }

    @Bean(name = "settlementItemWriter")
    public JpaItemWriter<Settlement> settlementItemWriter() {
        return new JpaItemWriterBuilder<Settlement>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
