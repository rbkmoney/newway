package com.rbkmoney.newway.service;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.newway.poller.event_stock.impl.rate.AbstractRateHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import com.rbkmoney.xrates.rate.Change;
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
    private final MachineEventParser<Change> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<SinkEvent> events) {
        events.forEach(this::handleIfAccept);
    }

    private void handleIfAccept(SinkEvent sinkEvent) {
        MachineEvent machineEvent = sinkEvent.getEvent();
        Change change = parser.parse(machineEvent);
        rateHandlers.stream()
                .filter(handler -> handler.accept(change))
                .forEach(handler -> handler.handle(change, machineEvent, null));

    }

}
