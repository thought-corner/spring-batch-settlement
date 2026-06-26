package com.project.settlement_batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.io.File;
import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Deprecated
@RequiredArgsConstructor
public class FileCleanupTasklet implements Tasklet {

    private final Clock clock;
    private final String rootPath;
    private final int retentionDays;

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate cutoffDate = resolveCutoffDate();
        File[] files = new File(rootPath).listFiles();

        if (files == null) {
            return RepeatStatus.FINISHED;
        }

        for (File file : files) {
            deleteIfExpired(file, cutoffDate);
        }
        return RepeatStatus.FINISHED;
    }

    private LocalDate resolveCutoffDate() {
        return LocalDate.now(clock).minusDays(retentionDays);
    }

    private void deleteIfExpired(File file, LocalDate cutoffDate) {
        String name = file.getName();
        if (!isDatedLogFile(name)) {
            return;
        }
        try {
            LocalDate fileDate = extractDate(name);
            if (fileDate.isBefore(cutoffDate)) {
                file.delete();
                log.info("삭제된 로그: {}", name);
            }
        } catch (Exception e) {
            log.warn("파일명에서 날짜를 추출할 수 없음: {}", name);
        }
    }

    private boolean isDatedLogFile(String name) {
        return name.endsWith(".log") && name.length() >= 10;
    }

    private LocalDate extractDate(String name) {
        String dateStr = name.substring(name.lastIndexOf("_") + 1, name.lastIndexOf("."));
        return LocalDate.parse(dateStr);
    }
}
