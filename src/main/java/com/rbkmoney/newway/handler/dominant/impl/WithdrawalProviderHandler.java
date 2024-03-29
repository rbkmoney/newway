package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.WithdrawalProviderObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.WithdrawalProviderDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalProvider;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WithdrawalProviderHandler
        extends AbstractDominantHandler<WithdrawalProviderObject, WithdrawalProvider, Integer> {

    private final WithdrawalProviderDaoImpl withdrawalProviderDao;

    public WithdrawalProviderHandler(WithdrawalProviderDaoImpl withdrawalProviderDao) {
        this.withdrawalProviderDao = withdrawalProviderDao;
    }

    @Override
    protected DomainObjectDao<WithdrawalProvider, Integer> getDomainObjectDao() {
        return withdrawalProviderDao;
    }

    @Override
    protected WithdrawalProviderObject getTargetObject() {
        return getDomainObject().getWithdrawalProvider();
    }

    @Override
    protected Integer getTargetObjectRefId() {
        return getTargetObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetWithdrawalProvider();
    }

    @Override
    public WithdrawalProvider convertToDatabaseObject(WithdrawalProviderObject withdrawalProviderObject, Long versionId,
                                                      boolean current) {
        WithdrawalProvider withdrawalProvider = new WithdrawalProvider();
        withdrawalProvider.setVersionId(versionId);
        withdrawalProvider.setWithdrawalProviderRefId(getTargetObjectRefId());
        var data = withdrawalProviderObject.getData();
        withdrawalProvider.setName(data.getName());
        withdrawalProvider.setDescription(data.getDescription());
        withdrawalProvider.setProxyRefId(data.getProxy().getRef().getId());
        withdrawalProvider.setProxyAdditionalJson(JsonUtil.objectToJsonString(data.getProxy().getAdditional()));
        withdrawalProvider.setIdentity(data.getIdentity());
        if (data.isSetWithdrawalTerms()) {
            withdrawalProvider.setWithdrawalTermsJson(JsonUtil.thriftBaseToJsonString(data.getWithdrawalTerms()));
        }
        if (data.isSetAccounts()) {
            Map<String, Long> accountsMap = data.getAccounts().entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().getSymbolicCode(), e -> e.getValue().getSettlement()));
            withdrawalProvider.setAccountsJson(JsonUtil.objectToJsonString(accountsMap));
        }
        withdrawalProvider.setCurrent(current);
        return withdrawalProvider;
    }
}
