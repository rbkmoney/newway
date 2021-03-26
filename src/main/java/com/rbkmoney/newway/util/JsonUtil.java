package com.rbkmoney.newway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.apache.thrift.TBase;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String thriftBaseToJsonString(TBase thriftBase) {
        try {
            return new TBaseProcessor().process(thriftBase, new JsonHandler()).toString();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't convert to json string: " + thriftBase, e);
        }
    }

    public static JsonNode thriftBaseToJsonNode(TBase thriftBase) {
        try {
            return new TBaseProcessor().process(thriftBase, new JsonHandler());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't convert to json node: " + thriftBase, e);
        }
    }

    public static String objectToJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't convert object to json string: " + o, e);
        }
    }
}
