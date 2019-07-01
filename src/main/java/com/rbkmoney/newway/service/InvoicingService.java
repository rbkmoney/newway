package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicingService implements EventService<MachineEvent, EventPayload> {

    private final List<AbstractInvoicingHandler> invoicingHandlers;

    @Override
    public Optional<Long> getLastEventId() {
        throw new RuntimeException("No longer supported");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(MachineEvent machineEvent, EventPayload payload) {
        try {
            List<InvoiceChange> invoiceChanges = payload.getInvoiceChanges();
            for (int i = 0; i < invoiceChanges.size(); i++) {
                InvoiceChange change = invoiceChanges.get(i);
                for (AbstractInvoicingHandler invoicingHandler : invoicingHandlers) {
                    if (invoicingHandler.accept(change)) {
                        invoicingHandler.handle(change, machineEvent, i);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Unexpected error while handling events; machineId: {},  eventId: {}", machineEvent.getSourceId(), machineEvent.getEventId(), e);
            throw e;
        }
    }
}
