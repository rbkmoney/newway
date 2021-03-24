package com.rbkmoney.newway.model;

import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import com.rbkmoney.newway.domain.tables.pojos.Payment;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWrapper {
    private Payment payment;
    private List<CashFlow> cashFlows;
    private boolean shouldInsert;
    private InvoicingKey key;

    public PaymentWrapper copy() {
        Payment paymentTarget = new Payment();
        BeanUtils.copyProperties(payment, paymentTarget);
        PaymentWrapper paymentWrapperTarget = new PaymentWrapper();
        paymentWrapperTarget.setKey(InvoicingKey.buildKey(this));
        paymentWrapperTarget.setPayment(paymentTarget);
        if (cashFlows != null) {
            List<CashFlow> cashFlowsTarget = new ArrayList<>();
            cashFlows.forEach(c -> {
                CashFlow cTarget = new CashFlow();
                BeanUtils.copyProperties(c, cTarget);
                cashFlowsTarget.add(cTarget);
            });
            paymentWrapperTarget.setCashFlows(cashFlowsTarget);
        }
        return paymentWrapperTarget;
    }
}
