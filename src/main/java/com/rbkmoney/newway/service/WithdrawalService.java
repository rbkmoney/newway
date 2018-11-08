package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.withdrawal.Event;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.impl.withdrawal.AbstractWithdrawalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalService implements EventService<SinkEvent, Event> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    private final List<AbstractWithdrawalHandler> withdrawalHandlers;

    public WithdrawalService(WithdrawalDao withdrawalDao, List<AbstractWithdrawalHandler> withdrawalHandlers) {
        this.withdrawalDao = withdrawalDao;
        this.withdrawalHandlers = withdrawalHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(SinkEvent sinkEvent, Event payload) {
        payload.getChanges().forEach(cc -> withdrawalHandlers.forEach(ph -> {
            if (ph.accept(cc)) {
                ph.handle(cc, sinkEvent);
            }
        }));
    }

    @Override
    public Optional<Long> getLastEventId() throws DaoException {
        Optional<Long> lastEventId = Optional.ofNullable(withdrawalDao.getLastEventId());
        log.info("Last withdrawal eventId={}", lastEventId);
        return lastEventId;
    }

}
