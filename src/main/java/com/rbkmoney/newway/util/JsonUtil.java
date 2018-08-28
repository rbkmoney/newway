package com.rbkmoney.newway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.apache.thrift.TBase;

import java.io.IOException;

public class JsonUtil {
    public static String toJsonString(TBase tBase) {
        try {
            return new TBaseProcessor().process(tBase, new JsonHandler()).toString();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't convert to json string: " + tBase, e);
        }
    }

    public static JsonNode toJsonNode(TBase tBase) {
        try {
            return new TBaseProcessor().process(tBase, new JsonHandler());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't convert to json node: " + tBase, e);
        }
    }
}
