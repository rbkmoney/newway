package com.rbkmoney.newway.serde;

import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalSessionChangeMachineEventParser extends MachineEventParser<TimestampedChange> {

    public WithdrawalSessionChangeMachineEventParser(BinaryDeserializer<TimestampedChange> deserializer) {
        super(deserializer);
    }
}