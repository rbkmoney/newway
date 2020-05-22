package com.rbkmoney.newway.serde;

import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import com.rbkmoney.xrates.rate.Change;
import com.rbkmoney.xrates.rate.Event;
import org.springframework.stereotype.Component;

@Component
public class RateMachineEventParser extends MachineEventParser<Change> {

    public RateMachineEventParser(BinaryDeserializer<Change> deserializer) {
        super(deserializer);
    }
}
