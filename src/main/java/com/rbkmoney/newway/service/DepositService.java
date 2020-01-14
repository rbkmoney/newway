package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.deposit.EventSinkPayload;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.impl.deposit.AbstractDepositHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepositService implements EventService<SinkEvent, EventSinkPayload> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DepositDao depositDao;

    private final List<AbstractDepositHandler> depositHandlers;

    public DepositService(DepositDao depositDao, List<AbstractDepositHandler> depositHandlers) {
        this.depositDao = depositDao;
        this.depositHandlers = depositHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, EventSinkPayload payload) {
        payload.getChanges().forEach(
                cc -> depositHandlers.forEach(ph -> {
                    if (ph.accept(cc)) {
                        ph.handle(cc, sinkEvent);
                    }
                }));
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(depositDao.getLastEventId());
        log.info("Last deposit eventId={}", lastEventId);
        return lastEventId;
    }

}
