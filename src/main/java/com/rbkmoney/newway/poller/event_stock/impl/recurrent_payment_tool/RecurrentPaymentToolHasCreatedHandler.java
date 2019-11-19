package com.rbkmoney.newway.poller.event_stock.impl.recurrent_payment_tool;

import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.DisposablePaymentResource;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolEvent;
import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolHasCreated;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.domain.enums.MobileOperatorType;
import com.rbkmoney.newway.domain.enums.PaymentToolType;
import com.rbkmoney.newway.domain.enums.RecurrentPaymentToolStatus;
import com.rbkmoney.newway.domain.tables.pojos.RecurrentPaymentTool;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
public class RecurrentPaymentToolHasCreatedHandler extends AbstractRecurrentPaymentToolHandler {

    private final RecurrentPaymentToolDao recurrentPaymentToolDao;
    private final Filter filter;

    public RecurrentPaymentToolHasCreatedHandler(RecurrentPaymentToolDao recurrentPaymentToolDao) {
        super(recurrentPaymentToolDao);
        this.recurrentPaymentToolDao = recurrentPaymentToolDao;
        this.filter = new PathConditionFilter(
                new PathConditionRule("rec_payment_tool_created", new IsNullCondition().not()));
    }

    @Override
    public void handle(RecurrentPaymentToolChange change, RecurrentPaymentToolEvent event, Integer changeId) {
        log.info("Start recurrent payment tool created handling, eventId={}, recurrent_payment_tool_id={}", event.getId(), event.getSource());
        RecurrentPaymentToolHasCreated recPaymentToolCreated = change.getRecPaymentToolCreated();
        var recurrentPaymentToolOrigin = recPaymentToolCreated.getRecPaymentTool();
        RecurrentPaymentTool recurrentPaymentTool = new RecurrentPaymentTool();
        setDefaultProperties(recurrentPaymentTool, event, changeId);
        recurrentPaymentTool.setRecurrentPaymentToolId(event.getSource());
        recurrentPaymentTool.setCreatedAt(TypeUtil.stringToLocalDateTime(recurrentPaymentToolOrigin.getCreatedAt()));
        recurrentPaymentTool.setPartyId(recurrentPaymentToolOrigin.getPartyId());
        recurrentPaymentTool.setShopId(recurrentPaymentToolOrigin.getShopId());
        recurrentPaymentTool.setPartyRevision(recurrentPaymentToolOrigin.getPartyRevision());
        recurrentPaymentTool.setDomainRevision(recurrentPaymentToolOrigin.getDomainRevision());
        recurrentPaymentTool.setStatus(TBaseUtil.unionFieldToEnum(recurrentPaymentToolOrigin.getStatus(), RecurrentPaymentToolStatus.class));
        DisposablePaymentResource paymentResource = recurrentPaymentToolOrigin.getPaymentResource();
        PaymentTool paymentTool = paymentResource.getPaymentTool();
        fillPaymentTool(recurrentPaymentTool, paymentTool);
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
        if (recurrentPaymentToolOrigin.isSetMinimalPaymentCost()) {
            recurrentPaymentTool.setAmount(recurrentPaymentToolOrigin.getMinimalPaymentCost().getAmount());
            recurrentPaymentTool.setCurrencyCode(recurrentPaymentToolOrigin.getMinimalPaymentCost().getCurrency().getSymbolicCode());
        }
        if (recPaymentToolCreated.isSetRiskScore()) {
            recurrentPaymentTool.setRiskScore(recPaymentToolCreated.getRiskScore().name());
        }
        recurrentPaymentToolDao.save(recurrentPaymentTool);
        log.info("End recurrent payment tool created handling, eventId={}, recurrent_payment_tool_id={}", event.getId(), event.getSource());
    }

    private void fillPaymentTool(RecurrentPaymentTool recurrentPaymentTool, PaymentTool paymentTool) {
        recurrentPaymentTool.setPaymentToolType(TBaseUtil.unionFieldToEnum(paymentTool, PaymentToolType.class));
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            recurrentPaymentTool.setBankCardToken(bankCard.getToken());
            recurrentPaymentTool.setBankCardPaymentSystem(bankCard.getPaymentSystem().name());
            recurrentPaymentTool.setBankCardBin(bankCard.getBin());
            recurrentPaymentTool.setBankCardMaskedPan(bankCard.getMaskedPan());
            if (bankCard.isSetTokenProvider()) {
                recurrentPaymentTool.setBankCardTokenProvider(bankCard.getTokenProvider().name());
            }
            if (bankCard.isSetIssuerCountry()) {
                recurrentPaymentTool.setBankCardIssuerCountry(bankCard.getIssuerCountry().name());
            }
            recurrentPaymentTool.setBankCardBankName(bankCard.getBankName());
            if (bankCard.isSetMetadata()) {
                recurrentPaymentTool.setBankCardMetadataJson(
                        JsonUtil.objectToJsonString(
                                bankCard.getMetadata().entrySet().stream()
                                        .collect(Collectors.toMap(
                                                e -> e.getKey(),
                                                e -> JsonUtil.tBaseToJsonNode(e.getValue())
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
        } else if (paymentTool.isSetPaymentTerminal()) {
            recurrentPaymentTool.setPaymentTerminalType(paymentTool.getPaymentTerminal().getTerminalType().name());
        } else if (paymentTool.isSetDigitalWallet()) {
            recurrentPaymentTool.setDigitalWalletId(paymentTool.getDigitalWallet().getId());
            recurrentPaymentTool.setDigitalWalletProvider(paymentTool.getDigitalWallet().getProvider().name());
            recurrentPaymentTool.setDigitalWalletToken(paymentTool.getDigitalWallet().getToken());
        } else if (paymentTool.isSetCryptoCurrency()) {
            recurrentPaymentTool.setCryptoCurrency(paymentTool.getCryptoCurrency().toString());
        } else if (paymentTool.isSetMobileCommerce()) {
            recurrentPaymentTool.setMobileCommerceOperator(TypeUtil.toEnumField(paymentTool.getMobileCommerce().getOperator().name(),
                    MobileOperatorType.class));
            recurrentPaymentTool.setMobileCommercePhoneCc(paymentTool.getMobileCommerce().getPhone().getCc());
            recurrentPaymentTool.setMobileCommercePhoneCtn(paymentTool.getMobileCommerce().getPhone().getCtn());
        }
    }

    @Override
    public Filter<RecurrentPaymentToolChange> getFilter() {
        return filter;
    }
}
