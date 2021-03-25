package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.ProviderObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.ProviderDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Provider;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProviderHandler extends AbstractDominantHandler<ProviderObject, Provider, Integer> {

    private final ProviderDaoImpl providerDao;

    public ProviderHandler(ProviderDaoImpl providerDao) {
        this.providerDao = providerDao;
    }

    @Override
    protected DomainObjectDao<Provider, Integer> getDomainObjectDao() {
        return providerDao;
    }

    @Override
    protected ProviderObject getTargetObject() {
        return getDomainObject().getProvider();
    }

    @Override
    protected Integer getTargetObjectRefId() {
        return getTargetObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetProvider();
    }

    @Override
    public Provider convertToDatabaseObject(ProviderObject providerObject, Long versionId, boolean current) {
        Provider provider = new Provider();
        provider.setVersionId(versionId);
        provider.setProviderRefId(getTargetObjectRefId());
        com.rbkmoney.damsel.domain.Provider data = providerObject.getData();
        provider.setName(data.getName());
        provider.setDescription(data.getDescription());
        provider.setProxyRefId(data.getProxy().getRef().getId());
        provider.setProxyAdditionalJson(JsonUtil.objectToJsonString(data.getProxy().getAdditional()));
        if (data.isSetTerminal()) {
            provider.setTerminalJson(JsonUtil.thriftBaseToJsonString(data.getTerminal()));
        }
        if (data.isSetAbsAccount()) {
            provider.setAbsAccount(data.getAbsAccount());
        }

        if (data.isSetTerms() && data.getTerms().isSetPayments()) {
            provider.setPaymentTermsJson(JsonUtil.thriftBaseToJsonString(data.getTerms().getPayments()));
        } else if (data.isSetPaymentTerms()) {
            provider.setPaymentTermsJson(JsonUtil.thriftBaseToJsonString(data.getPaymentTerms()));
        }

        if (data.isSetTerms() && data.getTerms().isSetRecurrentPaytools()) {
            provider.setRecurrentPaytoolTermsJson(
                    JsonUtil.thriftBaseToJsonString(data.getTerms().getRecurrentPaytools()));
        } else if (data.isSetRecurrentPaytoolTerms()) {
            provider.setRecurrentPaytoolTermsJson(JsonUtil.thriftBaseToJsonString(data.getRecurrentPaytoolTerms()));
        }

        if (data.isSetIdentity()) {
            provider.setIdentity(data.getIdentity());
        }
        if (data.isSetTerms() && data.getTerms().isSetWallet()) {
            provider.setWalletTermsJson(JsonUtil.thriftBaseToJsonString(data.getTerms().getWallet()));
        }
        if (data.isSetParamsSchema()) {
            provider.setParamsSchemaJson(
                    JsonUtil.objectToJsonString(
                            data.getParamsSchema().stream().map(
                                    JsonUtil::thriftBaseToJsonNode).collect(Collectors.toList())
                    )
            );
        }

        if (data.isSetAccounts()) {
            Map<String, Long> accountsMap = data.getAccounts().entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().getSymbolicCode(), e -> e.getValue().getSettlement()));
            provider.setAccountsJson(JsonUtil.objectToJsonString(accountsMap));
        }
        provider.setCurrent(current);
        return provider;
    }
}
