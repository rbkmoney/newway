package com.rbkmoney.newway.poller.event.stock.impl.destination;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.fistful.base.*;
import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.TimestampedChange;
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
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
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
public class DestinationCreatedHandler implements DestinationHandler {

    private final DestinationDao destinationDao;
    private final MachineEventCopyFactory<Destination, String> destinationMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        long sequenceId = event.getEventId();
        Change change = timestampedChange.getChange();
        String destinationId = event.getSourceId();
        log.info("Start destination created handling, sequenceId={}, destinationId={}", sequenceId, destinationId);

        Destination destination = destinationMachineEventCopyFactory
                .create(event, sequenceId, destinationId, timestampedChange.getOccuredAt());

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
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.thriftBaseToJsonNode(e.getValue())));
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
