package com.rbkmoney.newway.serde.deserializer;

import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.sink.common.serialization.impl.AbstractThriftBinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class SourceSessionChangeDeserializer extends AbstractThriftBinaryDeserializer<TimestampedChange> {

    @Override
    public TimestampedChange deserialize(byte[] bin) {
        return deserialize(bin, new TimestampedChange());
    }
}
