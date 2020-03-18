package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingMapper;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.PaymentWrapper;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicingService {

    private final List<AbstractInvoicingHandler> otherHandlers;
    private final List<AbstractInvoicingMapper<InvoiceWrapper>> invoiceMappers;
    private final List<AbstractInvoicingMapper<PaymentWrapper>> paymentMappers;
    private final InvoiceBatchService invoiceBatchService;
    private final PaymentBatchService paymentBatchService;
    private final MachineEventParser<EventPayload> parser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        LocalStorage storage = new LocalStorage();
        List<InvoiceWrapper> invoices = new ArrayList<>(machineEvents.size());
        List<PaymentWrapper> payments = new ArrayList<>(machineEvents.size());
        machineEvents.forEach(me -> {
            EventPayload payload = parser.parse(me);
            if (payload.isSetInvoiceChanges()) {
                try {
                    List<InvoiceChange> invoiceChanges = payload.getInvoiceChanges();
                    for (int i = 0; i < invoiceChanges.size(); i++) {
                        InvoiceChange change = invoiceChanges.get(i);
                        InvoiceWrapper invoiceWrapper = mapInvoice(change, me, i, storage);
                        PaymentWrapper paymentWrapper = mapPayment(change, me, i, storage);
                        if (invoiceWrapper != null) {
                            invoices.add(invoiceWrapper);
                            storage.put(InvoicingKey.builder()
                                    .invoiceId(invoiceWrapper.getInvoice().getInvoiceId())
                                    .type(InvoicingType.INVOICE)
                                    .build(), invoiceWrapper);
                        }
                        if (paymentWrapper != null) {
                            payments.add(paymentWrapper);
                            storage.put(InvoicingKey.builder()
                                    .invoiceId(paymentWrapper.getPayment().getInvoiceId())
                                    .paymentId(paymentWrapper.getPayment().getPaymentId())
                                    .type(InvoicingType.PAYMENT)
                                    .build(), paymentWrapper);
                        }
                        handleOtherEvent(change, me, i);
                    }
                } catch (Throwable e) {
                    log.error("Unexpected error while handling events; machineId: {},  eventId: {}", me.getSourceId(), me.getEventId(), e);
                    throw e;
                }
            }
        });
        if (!invoices.isEmpty()) {
            invoiceBatchService.process(invoices);
        }
        if (!payments.isEmpty()) {
            paymentBatchService.process(payments);
        }
    }

    private void handleOtherEvent(InvoiceChange change, MachineEvent me, int i) {
        otherHandlers.stream()
                .filter(m -> m.accept(change))
                .findFirst()
                .ifPresent(m -> m.handle(change, me, i));
    }

    private PaymentWrapper mapPayment(InvoiceChange change, MachineEvent me, int i, LocalStorage storage) {
        return paymentMappers.stream()
                .filter(m -> m.accept(change))
                .findFirst()
                .map(m -> m.map(change, me, i, storage))
                .orElse(null);
    }

    private InvoiceWrapper mapInvoice(InvoiceChange change, MachineEvent me, int i, LocalStorage storage) {
        return invoiceMappers.stream()
                .filter(m -> m.accept(change))
                .findFirst()
                .map(m -> m.map(change, me, i, storage))
                .orElse(null);
    }
}
