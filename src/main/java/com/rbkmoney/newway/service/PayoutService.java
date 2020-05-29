package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.poller.event_stock.impl.payout.AbstractPayoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PayoutService implements EventService<Event,EventPayload> {

    private final PayoutDao payoutDao;

    private final List<AbstractPayoutHandler> payoutHandlers;

    public PayoutService(PayoutDao payoutDao, List<AbstractPayoutHandler> payoutHandlers) {
        this.payoutDao = payoutDao;
        this.payoutHandlers = payoutHandlers;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(Event payoutEvent, EventPayload payload) {
        payload.getPayoutChanges().forEach(c -> payoutHandlers.forEach(ph -> {
            if (ph.accept(c)) {
                ph.handle(c, payoutEvent);
            }
        }));
    }

    @Override
    public Optional<Long> getLastEventId() {
        Optional<Long> lastEventId = Optional.ofNullable(payoutDao.getLastEventId());
        log.info("Last payout eventId={}", lastEventId);
        return lastEventId;
    }
}
