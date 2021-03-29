package com.rbkmoney.newway.mapper;

import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.newway.handler.event.stock.LocalStorage;

public interface Mapper<C, E, M> {
    default boolean accept(C change) {
        return getFilter().match(change);
    }

    M map(C change, E event, Integer changeId, LocalStorage storage);

    Filter getFilter();
}
