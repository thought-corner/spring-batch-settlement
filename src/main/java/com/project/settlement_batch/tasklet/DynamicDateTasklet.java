package com.project.settlement_batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@Deprecated
// @StepScope: 빈 생성을 Step 실행 순간까지 미루는 '늦은 바인딩'의 핵심.
// 없으면 기동 때 싱글톤으로 생성돼, 그 시점엔 jobParameters가 없어 SpEL 평가가 실패한다.
//@StepScope
public class DynamicDateTasklet implements Tasklet {

    private final String requestDate;

    /**
     * ex) ./gradlew bootRun --args='--spring.batch.job.enabled=true --spring.batch.job.name=dynamicDateJob requestDate=2026-06-26'
     *
     * <p>파라미터를 앱 시작 시점이 아니라 Step이 실제로 도는 시점에 가져온다(늦은 바인딩).
     * CLI 인자 → JobParameters로 변환되어 StepExecution에 실리고,
     * #{jobParameters['requestDate']} SpEL이 Step 스코프에서 그 맵을 조회해 값을 주입한다.
     */
    public DynamicDateTasklet(@Value("#{jobParameters['requestDate']}") String requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("외부에서 받은 날짜: {}", requestDate);
        log.info("이제 {} 날짜의 데이터를 처리합니다!", requestDate);
        return RepeatStatus.FINISHED;
    }
}
