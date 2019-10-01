package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingMapper;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InvoicingServiceTest {
    private List<AbstractInvoicingMapper<InvoiceWrapper>> wrongHandlers = new ArrayList<>();
    private List<AbstractInvoicingMapper<InvoiceWrapper>> rightHandlers = new ArrayList<>();

    @MockBean
    private InvoiceBatchService invoiceBatchService;
    @MockBean
    private PaymentBatchService paymentBatchService;
    @Mock
    private MachineEventParser parser;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        AbstractInvoicingMapper wrong = mock(AbstractInvoicingMapper.class);
        when(wrong.accept(any())).thenReturn(false);
        wrongHandlers.add(wrong);

        AbstractInvoicingMapper right = mock(AbstractInvoicingMapper.class);
        when(right.accept(any())).thenReturn(true);
        rightHandlers.add(right);
    }

    @Test
    public void handleEmptyChanges() {
        InvoicingService invoicingService = new InvoicingService(new ArrayList<>(), rightHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        EventPayload eventPayload = new EventPayload();
        Mockito.when(parser.parse(any())).thenReturn(eventPayload);

        invoicingService.handleEvents(Collections.singletonList(new MachineEvent()));

        verify(rightHandlers.get(0), times(0)).accept(any());
    }

    @Test
    public void handlerSupportsInvoicing() {
        InvoicingService invoicingService = new InvoicingService(new ArrayList<>(), rightHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent message = new MachineEvent();

        EventPayload eventPayload = new EventPayload();
        eventPayload.setInvoiceChanges(Collections.singletonList(new InvoiceChange()));
        Mockito.when(parser.parse(any())).thenReturn(eventPayload);

        invoicingService.handleEvents(Collections.singletonList(message));

        verify(rightHandlers.get(0), times(1)).accept(any());
        verify(rightHandlers.get(0), times(1)).map(any(), any(), any(), any());
    }

    @Test
    public void handlerNotSupportInvoicing() {
        InvoicingService invoicingService = new InvoicingService(new ArrayList<>(), wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        EventPayload eventPayload = new EventPayload();
        eventPayload.setInvoiceChanges(Collections.singletonList(new InvoiceChange()));
        Mockito.when(parser.parse(any())).thenReturn(eventPayload);

        invoicingService.handleEvents(Collections.singletonList(new MachineEvent()));

        verify(wrongHandlers.get(0), times(1)).accept(any());
        verify(wrongHandlers.get(0), times(0)).map(any(), any(), any(), any());
    }
}
