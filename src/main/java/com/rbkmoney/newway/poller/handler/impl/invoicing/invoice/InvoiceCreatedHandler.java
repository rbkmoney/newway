package com.rbkmoney.newway.poller.handler.impl.invoicing.invoice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceCartDao;
import com.rbkmoney.newway.dao.invoicing.iface.InvoiceDao;
import com.rbkmoney.newway.domain.enums.InvoiceStatus;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.poller.handler.impl.invoicing.AbstractInvoicingHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InvoiceCreatedHandler extends AbstractInvoicingHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceDao invoiceDao;

    private final InvoiceCartDao invoiceCartDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Filter filter;

    @Autowired
    public InvoiceCreatedHandler(InvoiceDao invoiceDao, InvoiceCartDao invoiceCartDao) {
        this.invoiceDao = invoiceDao;
        this.invoiceCartDao = invoiceCartDao;
        this.filter = new PathConditionFilter(new PathConditionRule("invoice_created", new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(InvoiceChange invoiceChange, Event event) throws DaoException {
        Invoice invoice = invoiceChange.getInvoiceCreated().getInvoice();
        long eventId = event.getId();

        log.info("Start invoice created handling, eventId={}, invoiceId={}, partyId={}, shopId={}",
                eventId, invoice.getId(), invoice.getOwnerId(), invoice.getShopId());

        com.rbkmoney.newway.domain.tables.pojos.Invoice invoiceRecord = new com.rbkmoney.newway.domain.tables.pojos.Invoice();
        invoiceRecord.setEventId(eventId);
        invoiceRecord.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        invoiceRecord.setInvoiceId(invoice.getId());
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

        long invId = invoiceDao.save(invoiceRecord);
        if (invoice.getDetails().isSetCart()) {
            List<InvoiceCart> invoiceCarts = invoice.getDetails().getCart().getLines().stream().map(il -> {
                InvoiceCart ic = new InvoiceCart();
                ic.setInvId(invId);
                ic.setProduct(il.getProduct());
                ic.setQuantity(il.getQuantity());
                ic.setAmount(il.getPrice().getAmount());
                ic.setCurrencyCode(il.getPrice().getCurrency().getSymbolicCode());
                Map<String, JsonNode> jsonNodeMap = il.getMetadata().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.toJsonNode(e.getValue())));
                try {
                    ic.setMetadataJson(objectMapper.writeValueAsString(jsonNodeMap));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Couldn't convert map to json string: " + jsonNodeMap, e);
                }
                return ic;
            }).collect(Collectors.toList());
            invoiceCartDao.save(invoiceCarts);
        }

        log.info("Invoice has been saved, eventId={}, invoiceId={}, partyId={}, shopId={}",
                eventId, invoice.getId(), invoice.getOwnerId(), invoice.getShopId());
    }

    @Override
    public Filter<InvoiceChange> getFilter() {
        return filter;
    }
}
