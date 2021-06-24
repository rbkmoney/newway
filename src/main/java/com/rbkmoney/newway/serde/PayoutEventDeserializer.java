package com.rbkmoney.newway.serde;

import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import com.rbkmoney.payout.manager.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayoutEventDeserializer extends AbstractThriftDeserializer<Event> {
    @Override
    public Event deserialize(String topic, byte[] data) {
        return deserialize(data, new Event());
    }
}