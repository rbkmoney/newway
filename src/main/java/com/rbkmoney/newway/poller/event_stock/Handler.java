package com.rbkmoney.newway.poller.event_stock;


import com.rbkmoney.geck.filter.Filter;

public interface Handler<T, E> {

    default boolean accept(T change) {
        return getFilter().match(change);
    }

    void handle(T change, E event);

    Filter<T> getFilter();

}
