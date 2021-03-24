package com.rbkmoney.newway.poller.event_stock;

import com.rbkmoney.geck.filter.Filter;

public interface Mapper<C, E, M> {
    default boolean accept(C change) {
        return getFilter().match(change);
    }

    M map(C change, E event, Integer changeId, LocalStorage storage);

    Filter getFilter();
}
