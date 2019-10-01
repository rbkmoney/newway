package com.rbkmoney.newway.poller.listener;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.exception.ParseException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.invoice.InvoiceCreatedHandler;
import com.rbkmoney.newway.service.InvoiceBatchService;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.service.PaymentBatchService;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;

public class InvoicingListenerTest {

    @Mock
    private InvoiceBatchService invoiceBatchService;
    @Mock
    private PaymentBatchService paymentBatchService;
    @Mock
    private MachineEventParser eventParser;
    @Mock
    private Acknowledgment ack;

    private InvoicingKafkaListener listener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        listener = new InvoicingKafkaListener(new InvoicingService(new ArrayList<>(), Collections.singletonList(new InvoiceCreatedHandler()), new ArrayList<>(), invoiceBatchService, paymentBatchService, eventParser));
    }

    @Test
    public void listenNonInvoiceChanges() {

        MachineEvent message = new MachineEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        payload.setCustomerChanges(List.of());
        event.setPayload(payload);
        Mockito.when(eventParser.parse(message)).thenReturn(payload);

        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);

        listener.handle(Collections.singletonList(new ConsumerRecord<>("topic", 1, 1, "kek", sinkEvent)), ack);

        Mockito.verify(invoiceBatchService, Mockito.times(0)).process(anyList());
        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }

    @Test(expected = ParseException.class)
    public void listenEmptyException() {
        MachineEvent message = new MachineEvent();

        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);

        Mockito.when(eventParser.parse(message)).thenThrow(new ParseException());

        listener.handle(Collections.singletonList(new ConsumerRecord<>("topic", 1, 1, "kek", sinkEvent)), ack);

        Mockito.verify(ack, Mockito.times(0)).acknowledge();
    }

    @Test
    public void listenChanges() {
        MachineEvent message = new MachineEvent();
        Event event = new Event();
        message.setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()));
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        InvoiceChange invoiceChange = new InvoiceChange();
        invoiceChange.setInvoiceCreated(
                new InvoiceCreated(
                        new Invoice()
                                .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                                .setStatus(InvoiceStatus.unpaid(new InvoiceUnpaid()))
                                .setDetails(new InvoiceDetails())
                                .setDue(TypeUtil.temporalToString(LocalDateTime.now()))
                                .setCost(new Cash().setAmount(1).setCurrency(new CurrencyRef()))
                                .setContext(new Content())));
        invoiceChanges.add(invoiceChange);
        payload.setInvoiceChanges(invoiceChanges);
        event.setPayload(payload);
        Mockito.when(eventParser.parse(message)).thenReturn(payload);

        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(message);

        listener.handle(Collections.singletonList(new ConsumerRecord<>("topic", 1, 1, "kek", sinkEvent)), ack);

        Mockito.verify(invoiceBatchService, Mockito.times(1)).process(anyList());
        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }
}
