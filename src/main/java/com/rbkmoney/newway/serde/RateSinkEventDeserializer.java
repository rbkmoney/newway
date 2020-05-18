package com.rbkmoney.newway.serde;

import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import com.rbkmoney.xrates.rate.SinkEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateSinkEventDeserializer extends AbstractThriftDeserializer<SinkEvent> {

    @Override
    public SinkEvent deserialize(String topic, byte[] data) {
        return deserialize(data, new SinkEvent());
    }

}