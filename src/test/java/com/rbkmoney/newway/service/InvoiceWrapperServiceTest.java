package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.AbstractAppDaoTests;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.*;

public class InvoiceWrapperServiceTest extends AbstractAppDaoTests {

    @Autowired
    private InvoiceWrapperService service;

    @Test
    public void getTest() {
        List<InvoiceWrapper> invoiceWrappers = IntStream.range(1, 5)
                .mapToObj(x -> new InvoiceWrapper(random(Invoice.class), randomListOf(3, InvoiceCart.class)))
                .collect(Collectors.toList());

        invoiceWrappers.forEach(iw -> {
            iw.getInvoice().setCurrent(false);
            iw.getCarts().forEach(c -> {
                c.setInvId(iw.getInvoice().getId());});
        });
        service.save(invoiceWrappers);

        InvoiceWrapper invoiceWrapper = service.get(invoiceWrappers.get(0).getInvoice().getInvoiceId(), new LocalStorage());
        assertEquals(invoiceWrappers.get(0).getInvoice().getShopId(), invoiceWrapper.getInvoice().getShopId());
    }
}
