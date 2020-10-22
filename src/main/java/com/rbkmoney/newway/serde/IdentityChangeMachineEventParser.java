package com.rbkmoney.newway.serde;

import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class IdentityChangeMachineEventParser extends MachineEventParser<TimestampedChange> {

    public IdentityChangeMachineEventParser(BinaryDeserializer<TimestampedChange> deserializer) {
        super(deserializer);
    }
}