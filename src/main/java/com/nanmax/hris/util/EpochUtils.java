package com.nanmax.hris.util;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
public class EpochUtils {
    public static Instant epochToInstant(Long epochSeconds) {
        if (epochSeconds == null) return null;
        return Instant.ofEpochSecond(epochSeconds);
    }
    public static Long instantToEpoch(Instant instant) {
        if (instant == null) return null;
        return instant.getEpochSecond();
    }
    public static LocalDate epochToLocalDate(Long epochSeconds) {
        if (epochSeconds == null) return null;
        return Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public static Long localDateToEpoch(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .getEpochSecond();
    }
    public static LocalDateTime epochToLocalDateTime(Long epochSeconds) {
        if (epochSeconds == null) return null;
        return Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    public static Long localDateTimeToEpoch(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.toInstant(ZoneOffset.UTC).getEpochSecond();
    }
    public static Long getCurrentEpoch() {
        return Instant.now().getEpochSecond();
    }
}