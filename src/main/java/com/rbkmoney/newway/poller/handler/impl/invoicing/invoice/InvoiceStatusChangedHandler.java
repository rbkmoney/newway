package com.rbkmoney.newway.poller.handler.impl.invoicing.invoice;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.enums.Invoicestatus;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoiceStatusChangedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    private final InvoiceCartDao invoiceCartDao;

    private final Filter filter;

    @Autowired
    public InvoiceStatusChangedHandler(InvoiceDao invoiceDao, InvoiceCartDao invoiceCartDao) {
        this.invoiceDao = invoiceDao;
        this.invoiceCartDao = invoiceCartDao;
        this.filter = new PathConditionFilter(new PathConditionRule("invoice_status_changed", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, Event event) throws DaoException {
        InvoiceStatus invoiceStatus = invoiceChange.getInvoiceStatusChanged().getStatus();
        long eventId = event.getId();

        com.rbkmoney.newway.domain.tables.pojos.Invoice invoiceSource = invoiceDao.get(event.getSource().getInvoiceId());
        if (invoiceSource == null) {
            throw new NotFoundException(String.format("Invoice not found, invoiceId='%s'", event.getSource().getInvoiceId()));
        }
        log.info("Start invoice status changed handling, eventId={}, invoiceId={}, partyId={}, shopId={}, status={}",
                eventId, invoiceSource.getInvoiceId(), invoiceSource.getPartyId(), invoiceSource.getShopId(), invoiceStatus.getSetField().getFieldName());

        Long invoiceSourceId = invoiceSource.getId();
        invoiceSource.setId(null);
        invoiceSource.setEventId(eventId);
        invoiceSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        Invoicestatus status = TypeUtil.toEnumField(invoiceStatus.getSetField().getFieldName(), Invoicestatus.class);
        if (status == null) {
            throw new IllegalArgumentException("Illegal invoice status: " + invoiceStatus);
        }
        invoiceSource.setStatus(status);
        if (invoiceStatus.isSetCancelled()) {
            invoiceSource.setStatusCancelledDetails(invoiceStatus.getCancelled().getDetails());
            invoiceSource.setStatusFulfilledDetails(null);
        } else if (invoiceStatus.isSetFulfilled()) {
            invoiceSource.setStatusCancelledDetails(null);
            invoiceSource.setStatusFulfilledDetails(invoiceStatus.getFulfilled().getDetails());
        }

        invoiceDao.update(invoiceSource.getInvoiceId());
        long invId = invoiceDao.save(invoiceSource);
        List<InvoiceCart> invoiceCartList = invoiceCartDao.getByInvId(invoiceSourceId);
        invoiceCartList.forEach(ic -> {
            ic.setId(null);
            ic.setInvId(invId);
        });
        invoiceCartDao.save(invoiceCartList);

        log.info("Invoice has been saved, eventId={}, invoiceId={}, partyId={}, shopId={}, status={}",
                eventId, invoiceSource.getInvoiceId(), invoiceSource.getPartyId(), invoiceSource.getShopId(), invoiceStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
