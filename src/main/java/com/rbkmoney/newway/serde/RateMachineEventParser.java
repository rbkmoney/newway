package com.rbkmoney.newway.serde;

import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.sink.common.serialization.BinaryDeserializer;
import com.rbkmoney.xrates.rate.Event;

public class RateMachineEventParser extends MachineEventParser<Event> {

    public RateMachineEventParser(BinaryDeserializer<Event> deserializer) {
        super(deserializer);
    }
}
