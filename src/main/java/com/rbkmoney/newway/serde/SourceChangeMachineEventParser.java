package com.rbkmoney.newway.serde;

import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class SourceChangeMachineEventParser extends MachineEventParser<TimestampedChange> {

    public SourceChangeMachineEventParser(BinaryDeserializer<TimestampedChange> deserializer) {
        super(deserializer);
    }
}