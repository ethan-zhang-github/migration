package priv.ethanzhang.pipeline.core.utils;

import java.time.*;
import java.util.Date;

/**
 * 时间类型转换
 */
public class TimeUtil {

    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDateTime instant2LocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Date localDateTime2Date(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date plusDuration(Date date, Duration duration) {
        return Date.from(date.toInstant().plus(duration));
    }

    public static Date changeZone(Date date, ZoneOffset offset) {
        return localDateTime2Date(changeZone(date2LocalDateTime(date), offset));
    }

    public static LocalDateTime changeZone(LocalDateTime time, ZoneOffset offset) {
        return time.atZone(getZone()).withZoneSameInstant(offset).toLocalDateTime();
    }

    public static ZoneOffset getZone() {
        return ZonedDateTime.now().getOffset();
    }

}
