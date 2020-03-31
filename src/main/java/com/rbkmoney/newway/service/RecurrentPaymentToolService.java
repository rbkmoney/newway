package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool.AbstractRecurrentPaymentToolHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurrentPaymentToolService {

    private final List<AbstractRecurrentPaymentToolHandler> handlers;
    private final MachineEventParser<RecurrentPaymentToolEventData> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> events) {
        events.forEach(event -> {
            AtomicInteger cnt = new AtomicInteger(0);
            List<RecurrentPaymentToolChange> changes = parser.parse(event).getChanges();
            changes.forEach(change -> {
                for (var handler : handlers) {
                    if (handler.accept(change)) {
                        handler.handle(change, event, cnt.getAndIncrement());
                        break;
                    }
                }
            });
        });
    }
}
