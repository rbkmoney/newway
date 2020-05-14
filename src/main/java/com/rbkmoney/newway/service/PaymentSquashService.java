package com.rbkmoney.newway.service;

import com.rbkmoney.newway.model.PaymentWrapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentSquashService extends SquashService<PaymentWrapper>{

    @Override
    protected void setId(PaymentWrapper paymentWrapper, Long id) {
        paymentWrapper.getPayment().setId(id);
        if (paymentWrapper.getCashFlows() != null) {
            paymentWrapper.getCashFlows().forEach(c -> {
                c.setId(null);
                c.setObjId(id);
            });
        }
    }

    @Override
    protected Long getId(PaymentWrapper paymentWrapper) {
        return paymentWrapper.getPayment().getId();
    }
}
