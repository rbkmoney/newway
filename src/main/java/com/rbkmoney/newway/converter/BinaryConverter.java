package com.rbkmoney.newway.converter;

public interface BinaryConverter<T> {

    T convert(byte[] bin, Class<T> clazz);

}
