package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.newway.handler.event.stock.impl.payout.PayoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {

    private final List<PayoutHandler> payoutHandlers;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<Event> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(Event event) {
        if (event.isSetPayload()) {
            EventPayload eventPayload = event.getPayload();
            for (int i = 0; i < eventPayload.getPayoutChanges().size(); i++) {
                PayoutChange change = eventPayload.getPayoutChanges().get(i);
                Integer changeId = i;
                payoutHandlers.stream()
                        .filter(handler -> handler.accept(change))
                        .forEach(handler -> handler.handle(change, event, changeId));
            }
        }
    }

}
