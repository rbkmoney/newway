package com.rbkmoney.newway.poller.event_stock.impl.destination;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.fistful.base.*;
import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.enums.DestinationResourceType;
import com.rbkmoney.newway.domain.enums.DestinationStatus;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DestinationCreatedHandler extends AbstractDestinationHandler {

    private final DestinationDao destinationDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("created", new IsNullCondition().not()));

    @Override
    public void handle(Change change, MachineEvent event) {
        long sequenceId = event.getEventId();
        String destinationId = event.getSourceId();
        log.info("Start destination created handling, sequenceId={}, destinationId={}", sequenceId, destinationId);
        Destination destination = new Destination();
        initDefaultFields(event, sequenceId, destinationId, destination);

        destination.setDestinationName(change.getCreated().getName());
        destination.setDestinationStatus(DestinationStatus.unauthorized);
        destination.setExternalId(change.getCreated().getExternalId());

        //TODO почему так было сделано?
        destination.setIdentityId(destinationId);
        if (change.getCreated().isSetCreatedAt()) {
            destination.setCreatedAt(TypeUtil.stringToLocalDateTime(change.getCreated().getCreatedAt()));
        }
        if (change.getCreated().isSetMetadata()) {
            Map<String, JsonNode> jsonNodeMap = change.getCreated().getMetadata().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
            destination.setContextJson(JsonUtil.objectToJsonString(jsonNodeMap));
        }
        Resource resource = change.getCreated().getResource();
        destination.setResourceType(TBaseUtil.unionFieldToEnum(resource, DestinationResourceType.class));
        if (resource.isSetBankCard()) {
            ResourceBankCard resourceBankCard = resource.getBankCard();
            BankCard bankCard = resourceBankCard.getBankCard();
            destination.setResourceBankCardToken(bankCard.getToken());
            destination.setResourceBankCardBin(bankCard.getBin());
            destination.setResourceBankCardMaskedPan(bankCard.getMaskedPan());
            destination.setResourceBankCardBankName(bankCard.getBankName());
            if (bankCard.isSetIssuerCountry()) {
                destination.setResourceBankCardIssuerCountry(bankCard.getIssuerCountry().toString());
            }
            if (bankCard.isSetPaymentSystem()) {
                destination.setResourceBankCardPaymentSystem(bankCard.getPaymentSystem().toString());
            }
            if (bankCard.isSetCardType()) {
                destination.setResourceBankCardType(bankCard.getCardType().toString());
            }
        } else if (resource.isSetCryptoWallet()) {
            ResourceCryptoWallet resourceCryptoWallet = resource.getCryptoWallet();
            CryptoWallet wallet = resourceCryptoWallet.getCryptoWallet();
            destination.setResourceCryptoWalletId(wallet.getId());
            destination.setResourceCryptoWalletType(wallet.getCurrency().name());
            if (wallet.isSetData()) {
                destination.setResourceCryptoWalletData(wallet.getData().getSetField().getFieldName());
            }
        }

        destinationDao.save(destination).ifPresentOrElse(
                dbContractId -> log.info("Destination created has been saved, sequenceId={}, destinationId={}",
                        sequenceId, destinationId),
                () -> log.info("Destination created bound duplicated,, sequenceId={}, destinationId={}",
                        sequenceId, destinationId));
    }

}
