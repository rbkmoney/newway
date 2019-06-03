package com.rbkmoney.newway.poller.event_stock.impl.destination;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.fistful.base.BankCard;
import com.rbkmoney.fistful.base.CryptoWallet;
import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.Resource;
import com.rbkmoney.fistful.destination.SinkEvent;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.enums.DestinationStatus;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DestinationCreatedHandler extends AbstractDestinationHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DestinationDao destinationDao;

    private final Filter filter;

    public DestinationCreatedHandler(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start destination created handling, eventId={}, destinationId={}", event.getId(), event.getSource());
        Destination destination = new Destination();
        destination.setEventId(event.getId());
        destination.setSequenceId(event.getPayload().getSequence());
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        destination.setDestinationId(event.getSource());
        destination.setDestinationName(change.getCreated().getName());
        destination.setDestinationStatus(DestinationStatus.unauthorized);
        destination.setExternalId(change.getCreated().getExternalId());

        destination.setIdentityId(change.getCreated().getId());
        if (change.getCreated().isSetStatus()) {
            destination.setDestinationStatus(TBaseUtil.unionFieldToEnum(change.getCreated().getStatus(), DestinationStatus.class));
        }
        if (change.getCreated().isSetCreatedAt()) {
            destination.setCreatedAt(TypeUtil.stringToLocalDateTime(change.getCreated().getCreatedAt()));
        }
        if (change.getCreated().isSetContext()) {
            Map<String, JsonNode> jsonNodeMap = change.getCreated().getContext().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
            destination.setContextJson(JsonUtil.objectToJsonString(jsonNodeMap));
        }
        Resource resource = change.getCreated().getResource();
        if (resource.isSetBankCard()) {
            BankCard bankCard = resource.getBankCard();
            destination.setResourceBankCardToken(bankCard.getToken());
            destination.setResourceBankCardBin(bankCard.getBin());
            destination.setResourceBankCardMaskedPan(bankCard.getMaskedPan());
            if (bankCard.isSetPaymentSystem()) {
                destination.setResourceBankCardPaymentSystem(bankCard.getPaymentSystem().toString());
            }
        } else if (resource.isSetCryptoWallet()) {
            CryptoWallet wallet = resource.getCryptoWallet();
            destination.setResourceCryptoWalletId(wallet.getId());
            destination.setResourceCryptoWalletType(wallet.getCurrency().toString());
        }

        destinationDao.updateNotCurrent(event.getSource());
        destinationDao.save(destination);
        log.info("Destination have been saved, eventId={}, destinationId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
