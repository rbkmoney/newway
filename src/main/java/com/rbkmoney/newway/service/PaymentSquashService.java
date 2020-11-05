package com.rbkmoney.newway.service;

import com.rbkmoney.newway.model.PaymentWrapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentSquashService {

    public List<PaymentWrapper> squash(List<PaymentWrapper> wrappers, List<Long> ids) {
        var groupedMap = wrappers.stream().collect(Collectors.groupingBy(PaymentWrapper::getKey, LinkedHashMap::new, Collectors.toList()));
        List<PaymentWrapper> result = new ArrayList<>();
        Iterator<Long> iterator = ids.iterator();
        groupedMap.forEach((key, pwList) -> {
            setIds(pwList, iterator);
            squashById(pwList, result);
            long count = result.stream().filter(w -> !w.isShouldInsert()).count();
            if (count > 1) {
                throw new IllegalStateException("Must be less or equal than one update statements");
            }
        });
        return result;
    }

    private void squashById(List<PaymentWrapper> pwList, List<PaymentWrapper> result) {
        var groupedById = pwList.stream().collect(Collectors.groupingBy(this::getId, LinkedHashMap::new, Collectors.toList()));
        groupedById.forEach((id, ws) -> {
            boolean shouldInsert = ws.get(0).isShouldInsert();
            PaymentWrapper pwLast = ws.get(ws.size() - 1);
            pwLast.setShouldInsert(shouldInsert);
            result.add(pwLast);
        });
    }

    private void setIds(List<PaymentWrapper> pwList, Iterator<Long> iterator) {
        PaymentWrapper wInsert = pwList.get(0);
        for (PaymentWrapper w : pwList) {
            Long id;
            if (w.isShouldInsert()) {
                wInsert = w;
                id = iterator.next();
            } else {
                id = getId(wInsert);
            }
            setId(w, id);
        }
    }

    private void setId(PaymentWrapper paymentWrapper, Long id) {
        paymentWrapper.getPayment().setId(id);
        if (paymentWrapper.getCashFlows() != null) {
            paymentWrapper.getCashFlows().forEach(c -> {
                c.setId(null);
                c.setObjId(id);
            });
        }
    }

    private Long getId(PaymentWrapper paymentWrapper) {
        return paymentWrapper.getPayment().getId();
    }
}
