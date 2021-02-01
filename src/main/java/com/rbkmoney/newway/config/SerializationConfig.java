package com.rbkmoney.newway.config;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventData;
import com.rbkmoney.geck.serializer.Geck;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.parser.impl.PartyEventDataMachineEventParser;
import com.rbkmoney.sink.common.parser.impl.PaymentEventPayloadMachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import com.rbkmoney.sink.common.serialization.impl.AbstractThriftBinaryDeserializer;
import com.rbkmoney.sink.common.serialization.impl.PartyEventDataDeserializer;
import com.rbkmoney.sink.common.serialization.impl.PaymentEventPayloadDeserializer;
import com.rbkmoney.xrates.rate.Change;
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

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.deposit.Event> depositEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.deposit.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.deposit.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.deposit.Event> depositEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.deposit.Event> depositEventDataBinaryDeserializer) {
        return new MachineEventParser<>(depositEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.destination.Event> destinationEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.destination.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.destination.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.destination.Event> destinationEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.destination.Event> destinationEventDataBinaryDeserializer) {
        return new MachineEventParser<>(destinationEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.identity.Event> identityEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.identity.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.identity.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.identity.Event> identityEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.identity.Event> identityEventDataBinaryDeserializer) {
        return new MachineEventParser<>(identityEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.source.Event> sourceEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.source.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.source.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.source.Event> sourceEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.source.Event> sourceEventDataBinaryDeserializer) {
        return new MachineEventParser<>(sourceEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.wallet.Event> walletEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.wallet.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.wallet.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.wallet.Event> walletEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.wallet.Event> walletEventDataBinaryDeserializer) {
        return new MachineEventParser<>(walletEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.withdrawal.Event> withdrawalEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.withdrawal.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.withdrawal.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.withdrawal.Event> withdrawalEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.withdrawal.Event> withdrawalEventDataBinaryDeserializer) {
        return new MachineEventParser<>(withdrawalEventDataBinaryDeserializer);
    }

    @Bean
    public BinaryDeserializer<com.rbkmoney.fistful.withdrawal_session.Event> withdrawalSessionEventDataBinaryDeserializer() {
        return new AbstractThriftBinaryDeserializer<>() {
            @Override
            public com.rbkmoney.fistful.withdrawal_session.Event deserialize(byte[] bytes) {
                return Geck.msgPackToTBase(bytes, com.rbkmoney.fistful.withdrawal_session.Event.class);
            }
        };
    }

    @Bean
    public MachineEventParser<com.rbkmoney.fistful.withdrawal_session.Event> withdrawalSessionEventDataMachineEventParser(
            BinaryDeserializer<com.rbkmoney.fistful.withdrawal_session.Event> withdrawalSessionEventDataBinaryDeserializer) {
        return new MachineEventParser<>(withdrawalSessionEventDataBinaryDeserializer);
    }
}
