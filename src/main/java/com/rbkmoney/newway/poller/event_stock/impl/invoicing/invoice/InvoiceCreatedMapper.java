package com.rbkmoney.newway.poller.event_stock.impl.invoicing.invoice;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.enums.InvoiceStatus;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.model.InvoiceWrapper;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.poller.event_stock.LocalStorage;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceCreatedMapper extends AbstractInvoicingInvoiceMapper {

    private Filter filter = new PathConditionFilter(
            new PathConditionRule("invoice_created", new IsNullCondition().not())
    );

    @Override
    public InvoiceWrapper map(InvoiceChange invoiceChange, MachineEvent event, Integer changeId, LocalStorage storage) throws DaoException {
        Invoice invoice = invoiceChange.getInvoiceCreated().getInvoice();
        long sequenceId = event.getEventId();
        String invoiceId = event.getSourceId();

        log.info("Start invoice created mapping, sequenceId={}, invoiceId={}, partyId={}, shopId={}",
                sequenceId, invoiceId, invoice.getOwnerId(), invoice.getShopId());

        com.rbkmoney.newway.domain.tables.pojos.Invoice invoiceRecord = new com.rbkmoney.newway.domain.tables.pojos.Invoice();
        setDefaultProperties(invoiceRecord, sequenceId, changeId, event.getCreatedAt());
        invoiceRecord.setInvoiceId(invoice.getId());
        invoiceRecord.setExternalId(invoice.getExternalId());
        invoiceRecord.setPartyId(invoice.getOwnerId());
        invoiceRecord.setShopId(invoice.getShopId());
        invoiceRecord.setPartyRevision(invoice.getPartyRevision());
        invoiceRecord.setCreatedAt(TypeUtil.stringToLocalDateTime(invoice.getCreatedAt()));
        InvoiceStatus status = TBaseUtil.unionFieldToEnum(invoice.getStatus(), InvoiceStatus.class);
        invoiceRecord.setStatus(status);
        if (invoice.getStatus().isSetCancelled()) {
            invoiceRecord.setStatusCancelledDetails(invoice.getStatus().getCancelled().getDetails());
        } else if (invoice.getStatus().isSetFulfilled()) {
            invoiceRecord.setStatusFulfilledDetails(invoice.getStatus().getFulfilled().getDetails());
        }
        invoiceRecord.setDetailsProduct(invoice.getDetails().getProduct());
        invoiceRecord.setDetailsDescription(invoice.getDetails().getDescription());
        invoiceRecord.setDue(TypeUtil.stringToLocalDateTime(invoice.getDue()));
        invoiceRecord.setAmount(invoice.getCost().getAmount());
        invoiceRecord.setCurrencyCode(invoice.getCost().getCurrency().getSymbolicCode());
        invoiceRecord.setContext(invoice.getContext().getData());
        invoiceRecord.setTemplateId(invoice.getTemplateId());

        InvoiceWrapper invoiceWrapper = new InvoiceWrapper();
        invoiceWrapper.setInvoice(invoiceRecord);
        if (invoice.getDetails().isSetCart()) {
            List<InvoiceCart> invoiceCarts = invoice.getDetails().getCart().getLines().stream().map(il -> {
                InvoiceCart ic = new InvoiceCart();
                ic.setProduct(il.getProduct());
                ic.setQuantity(il.getQuantity());
                ic.setAmount(il.getPrice().getAmount());
                ic.setCurrencyCode(il.getPrice().getCurrency().getSymbolicCode());
                Map<String, JsonNode> jsonNodeMap = il.getMetadata().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
                ic.setMetadataJson(JsonUtil.objectToJsonString(jsonNodeMap));
                return ic;
            }).collect(Collectors.toList());
            invoiceWrapper.setCarts(invoiceCarts);
        }
        log.info("Invoice has been mapped, sequenceId={}, invoiceId={}, partyId={}, shopId={}",
                sequenceId, invoiceId, invoice.getOwnerId(), invoice.getShopId());
        return invoiceWrapper;
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
