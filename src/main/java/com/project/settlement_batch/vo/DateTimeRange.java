package com.project.settlement_batch.vo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 시작~종료 시각 구간을 표현하는 값 객체(VO).
 *
 * <p>VO 원칙을 어떻게 만족하는지:
 * <ul>
 *   <li><b>불변</b> — record라 모든 필드가 final, setter 없음.
 *   <li><b>값 동등성</b> — record가 두 시각 기준 equals/hashCode를 생성. 같은 값이면 같은 객체.
 *   <li><b>자가 검증</b> — 컴팩트 생성자에서 불변식을 강제해, <i>유효하지 않은 구간은 존재 자체가 불가능</i>.
 *   <li><b>식별자 없음</b> — id가 아니라 '값'으로만 의미를 가진다.
 * </ul>
 *
 * <p>구간 해석은 <b>반열린 구간 [start, end)</b> 기준이다. DB 조회 시
 * {@code col >= start AND col < end} 와 맞아떨어져 경계값 중복 집계를 막는다.
 */
public record DateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {

    /**
     * 불변식 보장: null 금지, 시작은 종료보다 뒤일 수 없다.
     * 잘못된 인자는 생성 시점에 바로 막아, 이후 코드에서 유효성 검사를 반복하지 않게 한다.
     */
    public DateTimeRange {
        Objects.requireNonNull(startDateTime, "startDateTime must not be null");
        Objects.requireNonNull(endDateTime, "endDateTime must not be null");
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException(
                    "startDateTime must not be after endDateTime: %s ~ %s"
                            .formatted(startDateTime, endDateTime));
        }
    }

    public static DateTimeRange of(LocalDateTime start, LocalDateTime end) {
        return new DateTimeRange(start, end);
    }

    public static DateTimeRange ofDate(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return new DateTimeRange(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    public boolean contains(LocalDateTime target) {
        Objects.requireNonNull(target, "target must not be null");
        return !target.isBefore(startDateTime) && target.isBefore(endDateTime);
    }

    public Duration duration() {
        return Duration.between(startDateTime, endDateTime);
    }
}
