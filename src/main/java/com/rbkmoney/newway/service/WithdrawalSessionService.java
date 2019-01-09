package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.withdrawal_session.Event;
import com.rbkmoney.fistful.withdrawal_session.SinkEvent;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.impl.withdrawal_session.AbstractWithdrawalSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalSessionService implements EventService<SinkEvent, Event> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalSessionDao withdrawalSessionDao;

    private final List<AbstractWithdrawalSessionHandler> withdrawalSessionHandlers;

    public WithdrawalSessionService(WithdrawalSessionDao withdrawalSessionDao,
                                    List<AbstractWithdrawalSessionHandler> withdrawalSessionHandlers) {
        this.withdrawalSessionDao = withdrawalSessionDao;
        this.withdrawalSessionHandlers = withdrawalSessionHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(
                cc -> withdrawalSessionHandlers.forEach(ph -> {
                    if (ph.accept(cc)) {
                        ph.handle(cc, sinkEvent);
                    }
                }));
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(withdrawalSessionDao.getLastEventId());
        log.info("Last withdrawal session eventId={}", lastEventId);
        return lastEventId;
    }

}
