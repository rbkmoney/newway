package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.identity.Event;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.impl.identity.AbstractIdentityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IdentityService implements EventService<SinkEvent, Event> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    private final List<AbstractIdentityHandler> identityHandlers;

    public IdentityService(IdentityDao identityDao, List<AbstractIdentityHandler> identityHandlers) {
        this.identityDao = identityDao;
        this.identityHandlers = identityHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(cc -> identityHandlers.forEach(ph -> {
            if (ph.accept(cc)) {
                ph.handle(cc, sinkEvent);
            }
        }));
    }

    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(identityDao.getLastEventId());
        log.info("Last identity eventId={}", lastEventId);
        return lastEventId;
    }
}
