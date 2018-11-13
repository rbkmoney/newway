package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.source.Event;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.poller.event_stock.impl.source.AbstractSourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SourceService implements EventService<SinkEvent, Event> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SourceDao sourceDao;

    private final List<AbstractSourceHandler> sourceHandlers;

    public SourceService(SourceDao sourceDao, List<AbstractSourceHandler> sourceHandlers) {
        this.sourceDao = sourceDao;
        this.sourceHandlers = sourceHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(
                cc -> sourceHandlers.forEach(ph -> {
                    if (ph.accept(cc)) {
                        ph.handle(cc, sinkEvent);
                    }
                }));
    }

    @Override
    public Optional<Long> getLastEventId() {
        Optional<Long> lastEventId = Optional.ofNullable(sourceDao.getLastEventId());
        log.info("Last source eventId={}", lastEventId);
        return lastEventId;
    }

}
