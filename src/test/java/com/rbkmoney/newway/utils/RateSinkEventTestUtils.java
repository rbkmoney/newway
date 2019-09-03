package com.rbkmoney.newway.utils;

import com.rbkmoney.easyway.AbstractTestUtils;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

public class RateSinkEventTestUtils extends AbstractTestUtils {

    public static Dto create(String sourceId) {
        List<Quote> quotes = new ArrayList<Quote>() {{
            addAll(randomListOf(4, Quote.class));
        }};

        Event event = new Event(
                Collections.singletonList(
                        Change.created(
                                new ExchangeRateCreated(
                                        new ExchangeRateData(
                                                new TimestampInterval(
                                                        TypeUtil.temporalToString(random(LocalDateTime.class)),
                                                        TypeUtil.temporalToString(random(LocalDateTime.class))
                                                ),
                                                quotes
                                        )
                                )
                        )
                )
        );

        SinkEvent sinkEvent = new SinkEvent(
                random(Long.class),
                TypeUtil.temporalToString(random(LocalDateTime.class)),
                sourceId,
                event,
                random(Long.class)
        );
        return new Dto(event, sinkEvent);
    }

    public static class Dto {

        private Event event;
        private SinkEvent sinkEvent;

        public Dto(Event event, SinkEvent sinkEvent) {
            this.event = event;
            this.sinkEvent = sinkEvent;
        }

        public Event getEvent() {
            return event;
        }

        public SinkEvent getSinkEvent() {
            return sinkEvent;
        }
    }
}
