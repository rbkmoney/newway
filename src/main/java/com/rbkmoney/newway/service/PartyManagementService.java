package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.PartyManagementHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyManagementService {

    private final List<PartyManagementHandler> partyManagementHandlers;
    private final MachineEventParser<PartyEventData> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        PartyEventData eventPayload = parser.parse(machineEvent);
        if (eventPayload.isSetChanges()) {
            for (int i = 0; i < eventPayload.getChanges().size(); i++) {
                PartyChange partyChange = eventPayload.getChanges().get(i);
                Integer changeId = i;
                partyManagementHandlers.stream()
                        .filter(handler -> handler.accept(partyChange))
                        .forEach(handler -> handler.handle(partyChange, machineEvent, changeId));
            }
        }
    }

}
