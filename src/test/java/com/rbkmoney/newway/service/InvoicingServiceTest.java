package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.TestData;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.dao.invoicing.iface.ChargebackDao;
import com.rbkmoney.newway.dao.invoicing.iface.PaymentDao;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingMapper;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.chargeback.*;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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
    private PaymentDao paymentDao;
    @Mock
    private CashFlowService cashFlowService;
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

        when(paymentDao.get(any(), any())).thenReturn(EnhancedRandom.random(Payment.class));
    }

    @Test
    public void handleEmptyChanges() {
        InvoicingService invoicingService = new InvoicingService(new ArrayList<>(), rightHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        EventPayload eventPayload = new EventPayload();
        when(parser.parse(any())).thenReturn(eventPayload);

        invoicingService.handleEvents(Collections.singletonList(new MachineEvent()));

        verify(rightHandlers.get(0), times(0)).accept(any());
    }

    @Test
    public void handlerSupportsInvoicing() {
        InvoicingService invoicingService = new InvoicingService(new ArrayList<>(), rightHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent message = new MachineEvent();

        EventPayload eventPayload = new EventPayload();
        eventPayload.setInvoiceChanges(Collections.singletonList(new InvoiceChange()));
        when(parser.parse(any())).thenReturn(eventPayload);

        invoicingService.handleEvents(Collections.singletonList(message));

        verify(rightHandlers.get(0), times(1)).accept(any());
        verify(rightHandlers.get(0), times(1)).map(any(), any(), any(), any());
    }

    @Test
    public void handlerNotSupportInvoicing() {
        InvoicingService invoicingService = new InvoicingService(new ArrayList<>(), wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        EventPayload eventPayload = new EventPayload();
        eventPayload.setInvoiceChanges(Collections.singletonList(new InvoiceChange()));
        when(parser.parse(any())).thenReturn(eventPayload);

        invoicingService.handleEvents(Collections.singletonList(new MachineEvent()));

        verify(wrongHandlers.get(0), times(1)).accept(any());
        verify(wrongHandlers.get(0), times(0)).map(any(), any(), any(), any());
    }

    @Test
    public void handlerInvoicePaymentChargebackCreated() {
        ChargebackDao chargebackDao = mock(ChargebackDao.class);
        CashFlowDao cashFlowDao = mock(CashFlowDao.class);

        EventPayload eventPayload = new EventPayload();
        InvoiceChange invoiceChange = TestData.buildInvoiceChangeChargebackCreated();
        eventPayload.setInvoiceChanges(Collections.singletonList(invoiceChange));
        when(parser.parse(any())).thenReturn(eventPayload);

        List<AbstractInvoicingHandler> handlers = chargebackHandlers(chargebackDao, cashFlowDao, paymentDao);
        InvoicingService invoicingService = new InvoicingService(handlers, wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);
        MachineEvent machineEvent = buildMachineEvent();
        invoicingService.handleEvents(Collections.singletonList(machineEvent));

        Mockito.verify(chargebackDao, only()).save(any(Chargeback.class));
    }

    @Test
    public void handlerInvoicePaymentChargebackStatusChanged() {
        ChargebackDao chargebackDao = mockChargebackDao();
        CashFlowDao cashFlowDao = mock(CashFlowDao.class);

        EventPayload eventPayload = new EventPayload();
        InvoiceChange invoiceChange = TestData.buildInvoiceChangeChargebackStatusChanged();
        eventPayload.setInvoiceChanges(Collections.singletonList(invoiceChange));
        when(parser.parse(any())).thenReturn(eventPayload);

        List<AbstractInvoicingHandler> handlers = chargebackHandlers(chargebackDao, cashFlowDao, paymentDao);
        InvoicingService invoicingService = new InvoicingService(handlers, wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent machineEvent = buildMachineEvent();
        invoicingService.handleEvents(Collections.singletonList(machineEvent));

        Mockito.verify(chargebackDao, times(1)).save(any(Chargeback.class));
        Mockito.verify(chargebackDao, times(1)).updateNotCurrent(anyLong());
        Mockito.verify(cashFlowService, times(1)).save(anyLong(), anyLong(), any(PaymentChangeType.class));
    }

    @Test
    public void handlerInvoicePaymentChargebackLevyChanged() {
        ChargebackDao chargebackDao = mockChargebackDao();
        CashFlowDao cashFlowDao = mock(CashFlowDao.class);

        EventPayload eventPayload = new EventPayload();
        InvoiceChange invoiceChange = TestData.buildInvoiceChangeChargebackLevyChanged();
        eventPayload.setInvoiceChanges(Collections.singletonList(invoiceChange));
        when(parser.parse(any())).thenReturn(eventPayload);

        List<AbstractInvoicingHandler> handlers = chargebackHandlers(chargebackDao, cashFlowDao, paymentDao);
        InvoicingService invoicingService = new InvoicingService(handlers, wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent machineEvent = buildMachineEvent();
        invoicingService.handleEvents(Collections.singletonList(machineEvent));

        Mockito.verify(chargebackDao, times(1)).save(any(Chargeback.class));
        Mockito.verify(chargebackDao, times(1)).updateNotCurrent(anyLong());
        Mockito.verify(cashFlowService, times(1)).save(anyLong(), anyLong(), any(PaymentChangeType.class));
    }

    @Test
    public void handlerInvoicePaymentChargebackStageChanged() {
        ChargebackDao chargebackDao = mockChargebackDao();
        CashFlowDao cashFlowDao = mock(CashFlowDao.class);

        EventPayload eventPayload = new EventPayload();
        InvoiceChange invoiceChange = TestData.buildInvoiceChangeChargebackStageChanged();
        eventPayload.setInvoiceChanges(Collections.singletonList(invoiceChange));
        when(parser.parse(any())).thenReturn(eventPayload);
        List<AbstractInvoicingHandler> handlers = chargebackHandlers(chargebackDao, cashFlowDao, paymentDao);
        InvoicingService invoicingService = new InvoicingService(handlers, wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent machineEvent = buildMachineEvent();
        invoicingService.handleEvents(Collections.singletonList(machineEvent));

        Mockito.verify(chargebackDao, times(1)).save(any(Chargeback.class));
        Mockito.verify(chargebackDao, times(1)).updateNotCurrent(anyLong());
        Mockito.verify(cashFlowService, times(1)).save(anyLong(), anyLong(), any(PaymentChangeType.class));
    }

    @Test
    public void handlerInvoicePaymentChargebackCashFlowChanged() {
        ChargebackDao chargebackDao = mockChargebackDao();
        CashFlowDao cashFlowDao = mock(CashFlowDao.class);

        EventPayload eventPayload = new EventPayload();
        InvoiceChange invoiceChange = TestData.buildInvoiceChangeChargebackCashFlowChanged();
        eventPayload.setInvoiceChanges(Collections.singletonList(invoiceChange));
        when(parser.parse(any())).thenReturn(eventPayload);
        List<AbstractInvoicingHandler> handlers = chargebackHandlers(chargebackDao, cashFlowDao, paymentDao);
        InvoicingService invoicingService = new InvoicingService(handlers, wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent machineEvent = buildMachineEvent();
        invoicingService.handleEvents(Collections.singletonList(machineEvent));

        Mockito.verify(chargebackDao, times(1)).save(any(Chargeback.class));
        Mockito.verify(chargebackDao, times(1)).updateNotCurrent(anyLong());
        Mockito.verify(cashFlowService, times(1)).save(anyLong(), anyLong(), any(PaymentChangeType.class));
        Mockito.verify(cashFlowDao, times(1)).save(anyList());
    }

    @Test
    public void handlerInvoicePaymentChargebackBodyChanged() {
        ChargebackDao chargebackDao = mockChargebackDao();
        CashFlowDao cashFlowDao = mock(CashFlowDao.class);

        EventPayload eventPayload = new EventPayload();
        InvoiceChange invoiceChange = TestData.buildInvoiceChangeChargebackBodyChanged();
        eventPayload.setInvoiceChanges(Collections.singletonList(invoiceChange));
        when(parser.parse(any())).thenReturn(eventPayload);
        List<AbstractInvoicingHandler> handlers = chargebackHandlers(chargebackDao, cashFlowDao, paymentDao);
        InvoicingService invoicingService = new InvoicingService(handlers, wrongHandlers, new ArrayList<>(), invoiceBatchService, paymentBatchService, parser);

        MachineEvent machineEvent = buildMachineEvent();
        invoicingService.handleEvents(Collections.singletonList(machineEvent));

        Mockito.verify(chargebackDao, times(1)).save(any(Chargeback.class));
        Mockito.verify(chargebackDao, times(1)).updateNotCurrent(anyLong());
        Mockito.verify(cashFlowService, times(1)).save(anyLong(), anyLong(), any(PaymentChangeType.class));
    }

    private ChargebackDao mockChargebackDao() {
        ChargebackDao chargebackDao = mock(ChargebackDao.class);
        when(chargebackDao.get(anyString(), anyString(), anyString())).thenReturn(EnhancedRandom.random(Chargeback.class));
        when(chargebackDao.save(any(Chargeback.class))).thenReturn(1L);
        return chargebackDao;
    }

    private List<AbstractInvoicingHandler> chargebackHandlers(ChargebackDao chargebackDao, CashFlowDao cashFlowDao, PaymentDao paymentDao) {
        return Arrays.asList(
                new InvoicePaymentChargebackStageChangedHandler(chargebackDao, cashFlowService),
                new InvoicePaymentChargebackBodyChangedHandler(chargebackDao, cashFlowService),
                new InvoicePaymentChargebackCashFlowChangedHandler(chargebackDao, cashFlowService, cashFlowDao),
                new InvoicePaymentChargebackCreatedHandler(chargebackDao, paymentDao),
                new InvoicePaymentChargebackLevyChangedHandler(chargebackDao, cashFlowService),
                new InvoicePaymentChargebackStatusChangedHandler(chargebackDao, cashFlowService)
        );
    }

    private MachineEvent buildMachineEvent() {
        MachineEvent machineEvent = new MachineEvent();
        machineEvent.setSourceId("testSourceId");
        machineEvent.setCreatedAt(TypeUtil.temporalToString(Instant.now()));

        return machineEvent;
    }

}
