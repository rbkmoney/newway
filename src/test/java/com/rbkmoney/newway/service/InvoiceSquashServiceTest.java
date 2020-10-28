package com.rbkmoney.newway.service;

import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoiceWrapper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class InvoiceSquashServiceTest {

    @Test
    public void squashSimpleTest() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper InvoiceWrapper = buildInvoiceWrapper("inv_id",  true);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(InvoiceWrapper), List.of(1L));
        assertEquals(1, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
    }

    @Test
    public void squashSimple0Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper InvoiceWrapper = buildInvoiceWrapper("inv_id",  false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(InvoiceWrapper), List.of(1L));
        assertEquals(1, squashedWrappers.size());
        assertFalse(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
    }

    @Test
    public void squashSimple1Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id",  true);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id", 2L, false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(1, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(0).getInvoice().getPartyRevision(), pw2.getInvoice().getPartyRevision());
    }

    @Test
    public void squashSimple11Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id", false);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id", 2L, false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(1, squashedWrappers.size());
        assertFalse(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(0).getInvoice().getPartyRevision(), pw2.getInvoice().getPartyRevision());
    }

    @Test
    public void squashSimple12Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id", true);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id", 2L, true);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(0).getInvoice().getPartyRevision().longValue(), 0);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getInvoice().getId().longValue(), 2);
        assertEquals(squashedWrappers.get(1).getInvoice().getPartyRevision(), pw2.getInvoice().getPartyRevision());
    }

    @Test
    public void squashSimple2Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id", false);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id", 3L, true);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(2, squashedWrappers.size());
        assertFalse(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertEquals(squashedWrappers.get(0).getInvoice().getPartyRevision().longValue(), 0);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getInvoice().getId().longValue(), 2);
        assertEquals(squashedWrappers.get(1).getInvoice().getPartyRevision().longValue(), 3L);
    }

    @Test
    public void squashSimple3Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id", true);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id_2", false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2), List.of(1L, 2L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertFalse(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getInvoice().getId().longValue(), 2);
    }

    @Test
    public void squashSimple4Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id",  true);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id",  false);
        InvoiceWrapper pw3 = buildInvoiceWrapper("inv_id", false);
        InvoiceWrapper pw4 = buildInvoiceWrapper("inv_id",  true);
        InvoiceWrapper pw5 = buildInvoiceWrapper("inv_id",  false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2, pw3, pw4, pw5), List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getInvoice().getId().longValue(), 2);
    }

    @Test
    public void squashSimple41Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id",  true);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id", false);
        InvoiceWrapper pw3 = buildInvoiceWrapper("inv_id_2",  false);
        InvoiceWrapper pw4 = buildInvoiceWrapper("inv_id_2",  true);
        InvoiceWrapper pw5 = buildInvoiceWrapper("inv_id_2", 2L, false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2, pw3, pw4, pw5), List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(3, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertFalse(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getInvoice().getId().longValue(), 2);
        assertTrue(squashedWrappers.get(2).isShouldInsert());
        assertEquals(squashedWrappers.get(2).getInvoice().getId().longValue(), 3);
        assertEquals(squashedWrappers.get(2).getInvoice().getPartyRevision(), pw5.getInvoice().getPartyRevision());

    }


    @Test
    public void squashSimple42Test() {
        InvoiceSquashService service = new InvoiceSquashService();
        InvoiceWrapper pw1 = buildInvoiceWrapper("inv_id", true);
        InvoiceWrapper pw2 = buildInvoiceWrapper("inv_id", false);
        InvoiceWrapper pw3 = buildInvoiceWrapper("inv_id_2", true);
        InvoiceWrapper pw4 = buildInvoiceWrapper("inv_id_2", false);
        InvoiceWrapper pw5 = buildInvoiceWrapper("inv_id", false);
        List<InvoiceWrapper> squashedWrappers = service.squash(List.of(pw1, pw2, pw3, pw4, pw5), List.of(1L, 2L, 3L, 4L, 5L));
        assertEquals(2, squashedWrappers.size());
        assertTrue(squashedWrappers.get(0).isShouldInsert());
        assertEquals(squashedWrappers.get(0).getInvoice().getId().longValue(), 1);
        assertTrue(squashedWrappers.get(1).isShouldInsert());
        assertEquals(squashedWrappers.get(1).getInvoice().getId().longValue(), 2);
    }

    private InvoiceWrapper buildInvoiceWrapper(String invoiceId, Long partyRevision, boolean isShouldInsert) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(invoiceId);
        invoice.setPartyRevision(partyRevision);
        InvoiceWrapper InvoiceWrapper = new InvoiceWrapper(invoice, List.of(new InvoiceCart()));
        InvoiceWrapper.setKey(InvoicingKey.buildKey(invoiceId));
        InvoiceWrapper.setShouldInsert(isShouldInsert);
        return InvoiceWrapper;
    }

    private InvoiceWrapper buildInvoiceWrapper(String invoiceId, boolean isShouldInsert) {
        return buildInvoiceWrapper(invoiceId, 0L, isShouldInsert);
    }
}
