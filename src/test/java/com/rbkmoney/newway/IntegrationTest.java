package com.rbkmoney.newway;

import com.rbkmoney.damsel.domain.InvoiceFulfilled;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.damsel.payment_processing.InvoiceStatusChanged;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.service.InvoicingService;
import com.rbkmoney.newway.utils.MockUtils;
import com.rbkmoney.sink.common.serialization.impl.PaymentEventPayloadSerializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntegrationTest extends AbstractAppDaoTests {

    @Autowired
    private InvoicingService invoicingService;

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test(){

        PaymentEventPayloadSerializer serializer = new PaymentEventPayloadSerializer();

        String invoiceId = "inv_id";
        List<MachineEvent> machineEvents = List.of(
                new MachineEvent().setSourceId(invoiceId)
                        .setEventId(1)
                        .setCreatedAt(TypeUtil.temporalToString(LocalDateTime.now()))
                        .setData(Value.bin(serializer.serialize(
                                EventPayload.invoice_changes(
                                        List.of(InvoiceChange.invoice_created(new InvoiceCreated().setInvoice(MockUtils.buildInvoice(invoiceId))),
                                                InvoiceChange.invoice_status_changed(new InvoiceStatusChanged().setStatus(InvoiceStatus.fulfilled(new InvoiceFulfilled("keks")))))))))
        );
        invoicingService.handleEvents(machineEvents);
        assertEquals(2, jdbcTemplate.queryForObject("SELECT count(*) FROM nw.invoice WHERE invoice_id = ? ", new Object[]{invoiceId}, Integer.class).intValue());
        Invoice invoice = invoiceDao.get(invoiceId);
        assertEquals("fulfilled", invoice.getStatus().name());

    }
}
