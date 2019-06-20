package com.rbkmoney.newway.poller.event_stock.impl.invoicing.invoice;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.invoicing.AbstractInvoicingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceStatusChangedHandler extends AbstractInvoicingHandler {

    private final InvoiceDao invoiceDao;
    private final InvoiceCartDao invoiceCartDao;

    private Filter filter = new PathConditionFilter(
            new PathConditionRule("invoice_status_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, MachineEvent event, Integer changeId) throws DaoException {
        InvoiceStatus invoiceStatus = invoiceChange.getInvoiceStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();

        com.rbkmoney.newway.domain.tables.pojos.Invoice invoiceSource = invoiceDao.get(event.getSourceId());
        if (invoiceSource == null) {
            throw new NotFoundException(String.format("Invoice not found, invoiceId='%s'", event.getSourceId()));
        }
        log.info("Start invoice status changed handling, sequenceId={}, invoiceId={}, partyId={}, shopId={}, status={}",
                sequenceId, invoiceId, invoiceSource.getPartyId(), invoiceSource.getShopId(), invoiceStatus.getSetField().getFieldName());

        Long invoiceSourceId = invoiceSource.getId();
        invoiceSource.setId(null);
        invoiceSource.setWtime(null);
        invoiceSource.setChangeId(changeId);
        invoiceSource.setSequenceId(sequenceId);
        invoiceSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        invoiceSource.setStatus(TBaseUtil.unionFieldToEnum(invoiceStatus, com.rbkmoney.newway.domain.enums.InvoiceStatus.class));
        if (invoiceStatus.isSetCancelled()) {
            invoiceSource.setStatusCancelledDetails(invoiceStatus.getCancelled().getDetails());
            invoiceSource.setStatusFulfilledDetails(null);
        } else if (invoiceStatus.isSetFulfilled()) {
            invoiceSource.setStatusCancelledDetails(null);
            invoiceSource.setStatusFulfilledDetails(invoiceStatus.getFulfilled().getDetails());
        }

        Long invId = invoiceDao.save(invoiceSource);
        if (invId != null) {
            invoiceDao.updateNotCurrent(invoiceSourceId);
            List<InvoiceCart> invoiceCartList = invoiceCartDao.getByInvId(invoiceSourceId);
            invoiceCartList.forEach(ic -> {
                ic.setId(null);
                ic.setInvId(invId);
            });
            invoiceCartDao.save(invoiceCartList);
        }

        log.info("Invoice has been saved, sequenceId={}, invoiceId={}, partyId={}, shopId={}, status={}",
                sequenceId, invoiceId, invoiceSource.getPartyId(), invoiceSource.getShopId(), invoiceStatus.getSetField().getFieldName());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
