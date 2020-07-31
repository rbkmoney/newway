package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.Event;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.impl.wallet.AbstractWalletHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final MachineEventParser<Event> parser;
    private final List<AbstractWalletHandler> withdrawalHandlers;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        Event eventPayload = parser.parse(machineEvent);
        if (eventPayload.isSetChanges()) {
            for (int i = 0; i < eventPayload.getChanges().size(); i++) {
                Change change = eventPayload.getChanges().get(i);
                Integer changeId = i;
                withdrawalHandlers.stream()
                        .filter(handler -> handler.accept(change))
                        .forEach(handler -> handler.handle(change, machineEvent, changeId));
            }
        }
    }

}
