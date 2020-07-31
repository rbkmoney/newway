package com.rbkmoney.newway.service;


import com.rbkmoney.fistful.deposit.Event;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.impl.deposit.AbstractDepositHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final MachineEventParser<Event> parser;
    private final List<AbstractDepositHandler> withdrawalHandlers;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        Event eventPayload = parser.parse(machineEvent);
        if (eventPayload.isSetChange()) {
            withdrawalHandlers.stream()
                    .filter(handler -> handler.accept(eventPayload.getChange()))
                    .forEach(handler -> handler.handle(eventPayload.getChange(), machineEvent));
        }
    }

}
