package com.rbkmoney.newway.serde;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class DepositChangeMachineEventParser extends MachineEventParser<TimestampedChange> {

    public DepositChangeMachineEventParser(BinaryDeserializer<TimestampedChange> deserializer) {
        super(deserializer);
    }

}