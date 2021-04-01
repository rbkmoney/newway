package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.PaymentInstitutionObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.PaymentInstitutionDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.PaymentInstitution;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PaymentInstitutionHandler
        extends AbstractDominantHandler<PaymentInstitutionObject, PaymentInstitution, Integer> {

    private final PaymentInstitutionDaoImpl paymentInstitutionDao;

    public PaymentInstitutionHandler(PaymentInstitutionDaoImpl paymentInstitutionDao) {
        this.paymentInstitutionDao = paymentInstitutionDao;
    }

    @Override
    protected DomainObjectDao<PaymentInstitution, Integer> getDomainObjectDao() {
        return paymentInstitutionDao;
    }

    @Override
    protected PaymentInstitutionObject getTargetObject() {
        return getDomainObject().getPaymentInstitution();
    }

    @Override
    protected Integer getTargetObjectRefId() {
        return getTargetObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPaymentInstitution();
    }

    @Override
    public PaymentInstitution convertToDatabaseObject(PaymentInstitutionObject paymentInstitutionObject, Long versionId,
                                                      boolean current) {
        PaymentInstitution paymentInstitution = new PaymentInstitution();
        paymentInstitution.setVersionId(versionId);
        paymentInstitution.setPaymentInstitutionRefId(getTargetObjectRefId());
        com.rbkmoney.damsel.domain.PaymentInstitution data = paymentInstitutionObject.getData();
        paymentInstitution.setName(data.getName());
        paymentInstitution.setDescription(data.getDescription());
        if (data.isSetCalendar()) {
            paymentInstitution.setCalendarRefId(data.getCalendar().getId());
        }
        paymentInstitution.setSystemAccountSetJson(JsonUtil.thriftBaseToJsonString(data.getSystemAccountSet()));
        paymentInstitution
                .setDefaultContractTemplateJson(JsonUtil.thriftBaseToJsonString(data.getDefaultContractTemplate()));
        if (data.isSetDefaultWalletContractTemplate()) {
            paymentInstitution.setDefaultWalletContractTemplateJson(
                    JsonUtil.thriftBaseToJsonString(data.getDefaultWalletContractTemplate()));
        }
        if (data.isSetProviders()) {
            paymentInstitution.setProvidersJson(JsonUtil.thriftBaseToJsonString(data.getProviders()));
        }
        paymentInstitution.setInspectorJson(JsonUtil.thriftBaseToJsonString(data.getInspector()));
        paymentInstitution.setRealm(data.getRealm().name());
        paymentInstitution.setResidencesJson(
                JsonUtil.objectToJsonString(data.getResidences().stream().map(Enum::name).collect(Collectors.toSet())));
        paymentInstitution.setCurrent(current);
        return paymentInstitution;
    }
}
