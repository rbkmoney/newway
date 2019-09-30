package com.rbkmoney.newway.poller.event_stock.impl.invoicing.invoice;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.event_stock.*;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import com.rbkmoney.newway.service.InvoiceWrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceStatusChangedHandler extends AbstractInvoicingInvoiceMapper {

    private final InvoiceWrapperService invoiceWrapperService;

    private Filter filter = new PathConditionFilter(
            new PathConditionRule("invoice_status_changed", new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public InvoiceWrapper map(InvoiceChange invoiceChange, MachineEvent event, Integer changeId, LocalStorage storage) throws DaoException {
        InvoiceStatus invoiceStatus = invoiceChange.getInvoiceStatusChanged().getStatus();
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();

        InvoiceWrapper invoiceWrapper = invoiceWrapperService.get(invoiceId, storage);
        Invoice invoiceSource = invoiceWrapper.getInvoice();
        log.info("Start invoice status changed mapping, sequenceId={}, invoiceId={}, partyId={}, shopId={}, status={}",
                sequenceId, invoiceId, invoiceSource.getPartyId(), invoiceSource.getShopId(), invoiceStatus.getSetField().getFieldName());

        setDefaultProperties(invoiceSource, sequenceId, changeId, event.getCreatedAt());
        invoiceSource.setStatus(TBaseUtil.unionFieldToEnum(invoiceStatus, com.rbkmoney.newway.domain.enums.InvoiceStatus.class));
        if (invoiceStatus.isSetCancelled()) {
            invoiceSource.setStatusCancelledDetails(invoiceStatus.getCancelled().getDetails());
            invoiceSource.setStatusFulfilledDetails(null);
        } else if (invoiceStatus.isSetFulfilled()) {
            invoiceSource.setStatusCancelledDetails(null);
            invoiceSource.setStatusFulfilledDetails(invoiceStatus.getFulfilled().getDetails());
        }

        List<InvoiceCart> invoiceCarts = invoiceWrapper.getCarts();
        invoiceCarts.forEach(ic -> ic.setId(null));

        storage.put(InvoicingKey.builder().invoiceId(invoiceId).type(InvoicingType.INVOICE).build(), invoiceWrapper);

        log.info("Invoice has been mapped, sequenceId={}, invoiceId={}, partyId={}, shopId={}, status={}",
                sequenceId, invoiceId, invoiceSource.getPartyId(), invoiceSource.getShopId(), invoiceStatus.getSetField().getFieldName());
        return invoiceWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
