package com.rbkmoney;

import com.rbkmoney.geck.common.util.TypeUtil;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;

public abstract class AbstractTestUtils {

    protected LocalDateTime fromTime = LocalDateTime.now().minusHours(3);
    protected LocalDateTime toTime = LocalDateTime.now().minusHours(1);
    protected LocalDateTime inFromToPeriodTime = LocalDateTime.now().minusHours(2);

    protected static String generateDate() {
        return TypeUtil.temporalToString(LocalDateTime.now());
    }

    protected static Long generateLong() {
        return random(Long.class);
    }

    protected static Integer generateInt() {
        return random(Integer.class);
    }

    protected static String generateString() {
        return random(String.class);
    }

    protected static Instant generateCurrentTimePlusDay() {
        return now().plusDays(1).toInstant(getZoneOffset());
    }

    protected static ZoneOffset getZoneOffset() {
        return systemDefault().getRules().getOffset(now());
    }

    protected static String getContent(InputStream content) throws IOException {
        return IOUtils.toString(content, StandardCharsets.UTF_8);
    }
}
