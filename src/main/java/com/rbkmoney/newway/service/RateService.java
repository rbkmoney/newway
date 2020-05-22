package com.rbkmoney.newway.service;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.newway.poller.event_stock.impl.rate.AbstractRateHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.xrates.rate.Change;
import com.rbkmoney.xrates.rate.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {

    private final List<AbstractRateHandler> rateHandlers;
    private final MachineEventParser<Event> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<SinkEvent> events) {
        events.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(SinkEvent sinkEvent) {
        MachineEvent machineEvent = sinkEvent.getEvent();
        Event eventPayload = parser.parse(machineEvent);
        if (eventPayload.isSetChanges()) {
            for (int i = 0; i < eventPayload.getChanges().size(); i++) {
                Change change = eventPayload.getChanges().get(i);
                Integer changeId = i;
                rateHandlers.stream()
                        .filter(handler -> handler.accept(change))
                        .forEach(handler -> handler.handle(change, machineEvent, changeId));
            }
        }
    }

}
