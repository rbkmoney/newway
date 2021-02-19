package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.PayoutMethodDefinition;
import com.rbkmoney.damsel.domain.PayoutMethodObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.PayoutMethodDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.PayoutMethod;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import org.springframework.stereotype.Component;

@Component
public class PayoutMethodHandler extends AbstractDominantHandler<PayoutMethodObject, PayoutMethod, String> {

    private final PayoutMethodDaoImpl payoutMethodDao;

    public PayoutMethodHandler(PayoutMethodDaoImpl payoutMethodDao) {
        this.payoutMethodDao = payoutMethodDao;
    }

    @Override
    protected DomainObjectDao<PayoutMethod, String> getDomainObjectDao() {
        return payoutMethodDao;
    }

    @Override
    protected PayoutMethodObject getTargetObject() {
        return getDomainObject().getPayoutMethod();
    }

    @Override
    protected String getTargetObjectRefId() {
        return getTargetObject().getRef().getId().name();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPayoutMethod();
    }

    @Override
    public PayoutMethod convertToDatabaseObject(PayoutMethodObject payoutMethodObject, Long versionId, boolean current) {
        PayoutMethod payoutMethod = new PayoutMethod();
        payoutMethod.setVersionId(versionId);
        payoutMethod.setPayoutMethodRefId(getTargetObjectRefId());
        PayoutMethodDefinition data = payoutMethodObject.getData();
        payoutMethod.setName(data.getName());
        payoutMethod.setDescription(data.getDescription());
        payoutMethod.setCurrent(current);
        return payoutMethod;
    }
}
