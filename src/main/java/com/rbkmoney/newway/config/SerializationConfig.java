package com.rbkmoney.newway.config;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.parser.impl.PartyEventDataMachineEventParser;
import com.rbkmoney.sink.common.parser.impl.PaymentEventPayloadMachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import com.rbkmoney.sink.common.serialization.impl.PartyEventDataDeserializer;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventData;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.parser.impl.PaymentEventPayloadMachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import com.rbkmoney.sink.common.serialization.impl.AbstractThriftBinaryDeserializer;
import com.rbkmoney.sink.common.serialization.impl.PaymentEventPayloadDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializationConfig {

    @Bean
    public BinaryDeserializer<EventPayload> paymentEventPayloadDeserializer() {
        return new PaymentEventPayloadDeserializer();
    }

    @Bean
    public MachineEventParser<EventPayload> paymentEventPayloadMachineEventParser(BinaryDeserializer<EventPayload> paymentEventPayloadDeserializer) {
        return new PaymentEventPayloadMachineEventParser(paymentEventPayloadDeserializer);
    }

    @Bean
    public BinaryDeserializer<PartyEventData> partyEventDataBinaryDeserializer() {
        return new PartyEventDataDeserializer();
    }

    @Bean
    public MachineEventParser<PartyEventData> partyEventDataMachineEventParser(BinaryDeserializer<PartyEventData> partyEventDataBinaryDeserializer) {
        return new PartyEventDataMachineEventParser(partyEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<RecurrentPaymentToolEventData> recurrentPaymentToolEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public RecurrentPaymentToolEventData deserialize(byte[] bytes) {
                return deserialize(bytes, new RecurrentPaymentToolEventData());
            }
        };
    }

    @Bean
    public MachineEventParser<RecurrentPaymentToolEventData> recurrentPaymentToolEventDataMachineEventParser(
            BinaryDeserializer<RecurrentPaymentToolEventData> recurrentPaymentToolEventDataBinaryDeserializer
    ) {
        return new MachineEventParser<>(recurrentPaymentToolEventDataBinaryDeserializer);
    }
}
