package com.rbkmoney.newway.service;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.Event;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.impl.source.AbstractSourceHandler;
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
public class SourceService {

    private final List<AbstractSourceHandler> sourceHandlers;
    private final MachineEventParser<Event> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        machineEvents.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(MachineEvent machineEvent) {
        Event eventPayload = parser.parse(machineEvent);
        if (eventPayload.isSetChange()) {
            sourceHandlers.stream()
                    .filter(handler -> handler.accept(eventPayload.getChange()))
                    .forEach(handler -> handler.handle(eventPayload.getChange(), machineEvent));
        }
    }

}
