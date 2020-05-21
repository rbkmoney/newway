package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.impl.rate.AbstractRateHandler;
import com.rbkmoney.xrates.rate.Event;
import com.rbkmoney.xrates.rate.SinkEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService implements EventService<SinkEvent, Event> {

    private final RateDao rateDao;

    private final List<AbstractRateHandler> rateHandlers;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        AtomicInteger cnt = new AtomicInteger(0);
        payload.getChanges().forEach(
                cc -> rateHandlers.stream()
                        .filter(handler -> handler.accept(cc))
                        .forEach(ph -> ph.handle(cc, sinkEvent, cnt.getAndIncrement()))
        );
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(rateDao.getLastEventId());
        log.info("Last rate eventId={}", lastEventId);
        return lastEventId;
    }
}
