package com.rbkmoney.newway.utils;

import com.rbkmoney.easyway.AbstractTestUtils;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.sink.common.serialization.impl.ThriftBinarySerializer;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

public class RateSinkEventTestUtils extends AbstractTestUtils {

    public static List<SinkEvent> create(String sourceId, String... excludedFields) {
        ThriftBinarySerializer<Event> serializer = new ThriftBinarySerializer<>();

        List<Quote> quotes = randomListOf(4, Quote.class, excludedFields);
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(new MachineEvent()
                .setSourceId("")
                .setEventId(123L)
                .setCreatedAt("2016-03-22T06:12:27Z")
                .setSourceId(sourceId)
                .setData(Value.bin(serializer.serialize(
                        new Event(
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
                        )))));
        return Collections.singletonList(sinkEvent);
    }

}
