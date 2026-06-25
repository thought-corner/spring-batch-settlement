package com.project.settlement_batch.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@UtilityClass
public final class LogGeneratorUtils {

    private static final String ROOT_PATH = "./test-logs";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {
        File dir = new File(ROOT_PATH);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        createLogFile(dir, "access", 2);
        createLogFile(dir, "access", 0);
        createLogFile(dir, "service", 50);
        createLogFile(dir, "service", 100);

        createLogFile(dir, "system_config.conf", -1);
        log.info("테스트용 로그 파일 생성 완료! 경로 확인: {}", ROOT_PATH);
    }

    private static void createLogFile(File dir, String prefix, int daysAgo) throws IOException {
        String filename;

        if (daysAgo == -1) {
            filename = prefix;
        } else {
            LocalDate targetDate = LocalDate.now().minusDays(daysAgo);
            String dateStr = targetDate.format(DATE_FORMATTER);
            filename = prefix + "_" + dateStr + ".log";
        }

        File file = new File(dir, filename);

        if (file.createNewFile()) {
            log.info("파일 생성됨: {}", filename);
        } else {
            log.info("이미 존재함: {}", filename);
        }
    }
}
