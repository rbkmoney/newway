package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.destination.Event;
import com.rbkmoney.fistful.destination.SinkEvent;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.impl.destination.AbstractDestinationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationService implements EventService<SinkEvent, Event> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DestinationDao destinationDao;

    private final List<AbstractDestinationHandler> destinationHandlers;

    public DestinationService(DestinationDao destinationDao, List<AbstractDestinationHandler> destinationHandlers) {
        this.destinationDao = destinationDao;
        this.destinationHandlers = destinationHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(
                cc -> destinationHandlers.forEach(ph -> {
                    if (ph.accept(cc)) {
                        ph.handle(cc, sinkEvent);
                    }
                }));
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(destinationDao.getLastEventId());
        log.info("Last destination eventId={}", lastEventId);
        return lastEventId;
    }

}
