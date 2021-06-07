package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.PaymentMethodDaoImpl;
import com.rbkmoney.newway.domain.enums.PaymentMethodType;
import com.rbkmoney.newway.domain.tables.pojos.PaymentMethod;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.PaymentMethodUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class PaymentMethodHandler extends AbstractDominantHandler<PaymentMethodObject, PaymentMethod, String> {

    private static final String SEPARATOR = ".";
    private static final String DEPRECATED = "_deprecated";
    private final PaymentMethodDaoImpl paymentMethodDao;

    public PaymentMethodHandler(PaymentMethodDaoImpl paymentMethodDao) {
        this.paymentMethodDao = paymentMethodDao;
    }

    @Override
    protected DomainObjectDao<PaymentMethod, String> getDomainObjectDao() {
        return paymentMethodDao;
    }

    @Override
    protected PaymentMethodObject getTargetObject() {
        return getDomainObject().getPaymentMethod();
    }

    @Override
    protected String getTargetObjectRefId() {
        var paymentMethod = wrapPaymentMethod(getTargetObject().getRef().getId());

        Optional<String> paymentMethodRefId = PaymentMethodUtils.getPaymentMethodRefIdByBankCard(paymentMethod)
                .or(() -> PaymentMethodUtils.getPaymentMethodRefIdByPaymentTerminal(paymentMethod))
                .or(() -> PaymentMethodUtils.getPaymentMethodRefIdByDigitalWallet(paymentMethod))
                .or(() -> PaymentMethodUtils.getPaymentMethodRefIdByCryptoCurrency(paymentMethod))
                .or(() -> PaymentMethodUtils.getPaymentMethodRefIdByMobile(paymentMethod));

        if (paymentMethodRefId.isEmpty()) {
            throw new IllegalArgumentException("Unknown payment method: " + paymentMethod);
        }

        return getPaymentType(getTargetObject()) + SEPARATOR + paymentMethodRefId.get(); //NOSONAR
    }

    private Supplier<Optional<com.rbkmoney.damsel.domain.PaymentMethod>> wrapPaymentMethod(
            @NotNull com.rbkmoney.damsel.domain.PaymentMethod paymentMethod) {
        return () -> Optional.of(paymentMethod);
    }

    private String getPaymentType(PaymentMethodObject pmObj) {
        return pmObj.getRef().getId().getSetField().getFieldName().replaceAll(DEPRECATED, "");
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPaymentMethod();
    }

    @Override
    public PaymentMethod convertToDatabaseObject(
            PaymentMethodObject paymentMethodObject,
            Long versionId,
            boolean current) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setVersionId(versionId);
        paymentMethod.setPaymentMethodRefId(getTargetObjectRefId());
        var data = paymentMethodObject.getData();
        paymentMethod.setName(data.getName());
        paymentMethod.setDescription(data.getDescription());
        paymentMethod.setType(
                Enum.valueOf(
                        PaymentMethodType.class,
                        getPaymentType(paymentMethodObject)
                )
        );
        paymentMethod.setCurrent(current);
        return paymentMethod;
    }
}
