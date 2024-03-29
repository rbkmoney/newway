package com.rbkmoney.newway.handler.event.stock.impl.recurrent.payment.tool;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.DigitalWallet;
import com.rbkmoney.damsel.domain.DisposablePaymentResource;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolHasCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.mamsel.*;
import com.rbkmoney.newway.dao.recurrent.payment.tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.enums.MobileOperatorType;
import com.rbkmoney.newway.domain.enums.PaymentToolType;
import com.rbkmoney.newway.domain.enums.RecurrentPaymentToolStatus;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
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
public class RecurrentPaymentToolHasCreatedHandler implements RecurrentPaymentToolHandler {

    private final RecurrentPaymentToolDao recurrentPaymentToolDao;
    private final MachineEventCopyFactory<RecurrentPaymentTool, Integer> recurrentPaymentToolCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("rec_payment_tool_created", new IsNullCondition().not()));

    @Override
    public void handle(RecurrentPaymentToolChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        log.info("Start recurrent payment tool created handling, sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), sequenceId, changeId);
        RecurrentPaymentToolHasCreated recPaymentToolCreated = change.getRecPaymentToolCreated();
        var recurrentPaymentToolOrigin = recPaymentToolCreated.getRecPaymentTool();
        RecurrentPaymentTool recurrentPaymentTool =
                recurrentPaymentToolCopyFactory.create(event, sequenceId, changeId, null);
        recurrentPaymentTool.setRecurrentPaymentToolId(event.getSourceId());
        recurrentPaymentTool.setCreatedAt(TypeUtil.stringToLocalDateTime(recurrentPaymentToolOrigin.getCreatedAt()));
        recurrentPaymentTool.setPartyId(recurrentPaymentToolOrigin.getPartyId());
        recurrentPaymentTool.setShopId(recurrentPaymentToolOrigin.getShopId());
        recurrentPaymentTool.setPartyRevision(recurrentPaymentToolOrigin.getPartyRevision());
        recurrentPaymentTool.setDomainRevision(recurrentPaymentToolOrigin.getDomainRevision());
        recurrentPaymentTool.setStatus(
                TBaseUtil.unionFieldToEnum(recurrentPaymentToolOrigin.getStatus(), RecurrentPaymentToolStatus.class));
        DisposablePaymentResource paymentResource = recurrentPaymentToolOrigin.getPaymentResource();
        fillPaymentTool(recurrentPaymentTool, paymentResource.getPaymentTool());
        recurrentPaymentTool.setPaymentSessionId(paymentResource.getPaymentSessionId());
        if (paymentResource.isSetClientInfo()) {
            recurrentPaymentTool.setClientInfoIpAddress(paymentResource.getClientInfo().getIpAddress());
            recurrentPaymentTool.setClientInfoFingerprint(paymentResource.getClientInfo().getFingerprint());
        }
        recurrentPaymentTool.setRecToken(recurrentPaymentToolOrigin.getRecToken());
        if (recurrentPaymentToolOrigin.isSetRoute()) {
            recurrentPaymentTool.setRouteProviderId(recurrentPaymentToolOrigin.getRoute().getProvider().getId());
            recurrentPaymentTool.setRouteTerminalId(recurrentPaymentToolOrigin.getRoute().getTerminal().getId());
        }
        if (recPaymentToolCreated.isSetRoute()) {
            recurrentPaymentTool.setRouteProviderId(recPaymentToolCreated.getRoute().getProvider().getId());
            recurrentPaymentTool.setRouteTerminalId(recPaymentToolCreated.getRoute().getTerminal().getId());
        }
        if (recurrentPaymentToolOrigin.isSetMinimalPaymentCost()) {
            recurrentPaymentTool.setAmount(recurrentPaymentToolOrigin.getMinimalPaymentCost().getAmount());
            recurrentPaymentTool.setCurrencyCode(
                    recurrentPaymentToolOrigin.getMinimalPaymentCost().getCurrency().getSymbolicCode());
        }
        if (recPaymentToolCreated.isSetRiskScore()) {
            recurrentPaymentTool.setRiskScore(recPaymentToolCreated.getRiskScore().name());
        }
        recurrentPaymentToolDao.save(recurrentPaymentTool);
        log.info("End recurrent payment tool created handling, sourceId={}, sequenceId={}, changeId={}",
                event.getSourceId(), sequenceId, changeId);
    }

    private void fillPaymentTool(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        recurrentPaymentTool.setPaymentToolType(TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class));
        if (paymentTool.isSetBankCard()) {
            fillBankCard(recurrentPaymentTool, paymentTool);
        } else if (paymentTool.isSetPaymentTerminal()) {
            fillPaymentTerminal(recurrentPaymentTool, paymentTool);
        } else if (paymentTool.isSetDigitalWallet()) {
            fillDigitalWallet(recurrentPaymentTool, paymentTool);
        } else if (CryptoCurrencyUtil.isSetCryptoCurrency(paymentTool)) {
            fillCryptoCurrency(recurrentPaymentTool, paymentTool);
        } else if (paymentTool.isSetMobileCommerce()) {
            fillMobileCommerce(recurrentPaymentTool, paymentTool);
        }
    }

    private void fillMobileCommerce(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        recurrentPaymentTool.setMobileCommerceOperator(
                MobileOperatorUtil.getMobileOperatorName(paymentTool.getMobileCommerce()));
        recurrentPaymentTool.setMobileCommercePhoneCc(paymentTool.getMobileCommerce().getPhone().getCc());
        recurrentPaymentTool.setMobileCommercePhoneCtn(paymentTool.getMobileCommerce().getPhone().getCtn());
    }

    private void fillCryptoCurrency(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        recurrentPaymentTool.setCryptoCurrency(CryptoCurrencyUtil.getCryptoCurrencyName(paymentTool));
    }

    private void fillDigitalWallet(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        DigitalWallet digitalWallet = paymentTool.getDigitalWallet();
        recurrentPaymentTool.setDigitalWalletId(digitalWallet.getId());
        recurrentPaymentTool.setDigitalWalletProvider(DigitalWalletUtil.getDigitalWalletName(digitalWallet));
        recurrentPaymentTool.setDigitalWalletToken(digitalWallet.getToken());
    }

    private void fillPaymentTerminal(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        recurrentPaymentTool.setPaymentTerminalType(
                TerminalPaymentUtil.getTerminalPaymentProviderName(paymentTool.getPaymentTerminal()));
    }

    private void fillBankCard(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        BankCard bankCard = paymentTool.getBankCard();
        recurrentPaymentTool.setBankCardToken(bankCard.getToken());
        recurrentPaymentTool.setBankCardPaymentSystem(PaymentSystemUtil.getPaymentSystemName(bankCard));
        recurrentPaymentTool.setBankCardBin(bankCard.getBin());
        recurrentPaymentTool.setBankCardMaskedPan(bankCard.getLastDigits());
        recurrentPaymentTool.setBankCardTokenProvider(TokenProviderUtil.getTokenProviderName(bankCard));
        if (bankCard.isSetIssuerCountry()) {
            recurrentPaymentTool.setBankCardIssuerCountry(bankCard.getIssuerCountry().name());
        }
        recurrentPaymentTool.setBankCardBankName(bankCard.getBankName());
        if (bankCard.isSetMetadata()) {
            recurrentPaymentTool.setBankCardMetadataJson(JsonUtil.objectToJsonString(
                    bankCard.getMetadata().entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> JsonUtil.thriftBaseToJsonNode(e.getValue())
                    ))));
        }
        if (bankCard.isSetIsCvvEmpty()) {
            recurrentPaymentTool.setBankCardIsCvvEmpty(bankCard.isIsCvvEmpty());
        }
        if (bankCard.isSetExpDate()) {
            recurrentPaymentTool.setBankCardExpDateMonth((int) bankCard.getExpDate().getMonth());
            recurrentPaymentTool.setBankCardExpDateYear((int) bankCard.getExpDate().getYear());
        }
        recurrentPaymentTool.setBankCardCardholderName(bankCard.getCardholderName());
    }

    @Override
    public Filter<RecurrentPaymentToolChange> getFilter() {
        return filter;
    }
}
