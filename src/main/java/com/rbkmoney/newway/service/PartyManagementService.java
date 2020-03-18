package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyManagementService {

    private final PartyDao partyDao;
    private final List<AbstractPartyManagementHandler> partyManagementHandlers;
    private final MachineEventParser<EventPayload> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        EventPayload eventPayload = parser.parse(machineEvent);
        if(eventPayload.isSetPartyChanges()){
            eventPayload.getPartyChanges().
                forEach(payload -> partyManagementHandlers.stream()
                            .filter(handler -> handler.accept(payload))
                            .findFirst()
                            .ifPresent(handler -> handler.handle(payload, machineEvent))
                );
        }
    }

}
