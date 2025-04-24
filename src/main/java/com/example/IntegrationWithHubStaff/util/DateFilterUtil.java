package com.example.IntegrationWithHubStaff.util;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DateFilterUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final ZoneId ZONE = ZoneId.systemDefault();

    public static <T> List<T> filterFromPreviousDay(List<T> list, Function<T, String> createdAtExtractor) {
        LocalDate yesterday = LocalDate.now(ZONE).minusDays(1);
        Instant startOfDay = yesterday.atStartOfDay(ZONE).toInstant();
        Instant endOfDay = yesterday.atTime(23, 59, 59).atZone(ZONE).toInstant();

        return list.stream()
                .filter(item -> {
                    try {
                        String createdAtStr = createdAtExtractor.apply(item);
                        if (createdAtStr == null) return false;

                        Instant createdAt = LocalDateTime.parse(createdAtStr, FORMATTER)
                                .atZone(ZONE)
                                .toInstant();

                        return !createdAt.isBefore(startOfDay) && !createdAt.isAfter(endOfDay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}
