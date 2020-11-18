package com.rbkmoney.newway.serde;

import com.rbkmoney.damsel.payout_processing.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Map;

@Slf4j
public class PayoutEventDeserializer implements Deserializer<Event> {

    ThreadLocal<TDeserializer> tDeserializerThreadLocal = ThreadLocal.withInitial(() -> new TDeserializer(new TBinaryProtocol.Factory()));

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Event deserialize(String topic, byte[] data) {
        log.debug("Message, topic: {}, byteLength: {}", topic, data.length);
        Event payoutEvent = new Event();

        try {
            tDeserializerThreadLocal.get().deserialize(payoutEvent, data);
        } catch (Exception e) {
            log.error("Error when deserialize ruleTemplate data: {} ", data, e);
        }

        return payoutEvent;
    }

    @Override
    public void close() {
    }
}