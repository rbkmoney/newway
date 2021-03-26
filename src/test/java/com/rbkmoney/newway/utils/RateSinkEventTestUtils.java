package com.rbkmoney.newway.utils;

import com.rbkmoney.easyway.AbstractTestUtils;
import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.xrates.base.TimestampInterval;
import com.rbkmoney.xrates.rate.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

public class RateSinkEventTestUtils extends AbstractTestUtils {

    public static List<SinkEvent> create(String sourceId, String... excludedFields) {
        List<Quote> quotes = randomListOf(4, Quote.class, excludedFields);
        quotes.stream().forEach(quote -> {
            quote.getDestination().setExponent((short) 2);
            quote.getSource().setExponent((short) 2);
            quote.getExchangeRate().setQ(1L);
            quote.getExchangeRate().setP(1L);
        });
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(new MachineEvent()
                .setEventId(123L)
                .setCreatedAt("2016-03-22T06:12:27Z")
                .setSourceId(sourceId)
                .setData(Value.bin(Geck.toMsgPack(
                        Change.created(
                                new ExchangeRateCreated(
                                        new ExchangeRateData(
                                                new TimestampInterval(
                                                        Instant.now().toString(),
                                                        Instant.now().toString()
                                                ),
                                                quotes
                                        )
                                )
                        ))
                )));
        return Collections.singletonList(sinkEvent);
    }

}
