package com.rbkmoney.newway.service;

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

    private final List<AbstractRecurrentPaymentToolHandler> recurrentPaymentToolHandlers;
    private final MachineEventParser<RecurrentPaymentToolEventData> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> events) {
        events.forEach(event -> {
            AtomicInteger cnt = new AtomicInteger(0);
            RecurrentPaymentToolEventData recurrentPaymentToolEventData = parser.parse(event);
            recurrentPaymentToolEventData.getChanges().forEach(
                    change -> recurrentPaymentToolHandlers.forEach
                            (handler -> {
                                if (handler.accept(change)) {
                                    handler.handle(change, event, cnt.getAndIncrement());
                                }
                            }));
        });

    }
}
