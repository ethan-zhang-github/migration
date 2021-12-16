package com.aihuishou.pipeline.core.utils;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期格式化
 */
@Getter
public enum DateTimeFormatters {

    PATTERN_0("yyyy-MM-dd HH:mm:ss"),
    PATTERN_1("yyyy-MM-dd HH:mm:ss:SSS"),
    PATTERN_2("yyyy-MM-dd"),
    PATTERN_3("yyyyMMdd")
    ;

    private final Map<String, DateTimeFormatter> cache = new ConcurrentHashMap<>();

    private final String pattern;

    DateTimeFormatters(String pattern) {
        this.pattern = pattern;
    }
    
    public LocalDateTime parse(String src) {
        return LocalDateTime.parse(src, DateTimeFormatter.ofPattern(pattern));
    }

    public Date parse2Date(String src) {
        return Date.from(parse(src).atZone(ZoneId.systemDefault()).toInstant());
    }

    public String formatNow() {
        return formatLocalDateTime(LocalDateTime.now());
    }

    public String formatInstant(Instant instant) {
        return formatDate(new Date(instant.toEpochMilli()));
    }

    public String formatLocalDateTime(LocalDateTime localDateTime) {
        return getFormatter().format(localDateTime);
    }

    public String formatDate(Date date) {
        return formatLocalDateTime(TimeUtil.date2LocalDateTime(date));
    }

    private DateTimeFormatter getFormatter() {
        return cache.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

}
