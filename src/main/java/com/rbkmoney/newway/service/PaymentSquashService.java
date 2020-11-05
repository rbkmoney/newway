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
            setIds(iterator, pwList);
            squashById(result, pwList);
            long count = result.stream().filter(w -> !w.isShouldInsert()).count();
            if (count > 1) {
                throw new IllegalStateException("Must be less or equal than one update statements");
            }
        });
        return result;
    }

    private void squashById(List<PaymentWrapper> result, List<PaymentWrapper> pwList) {
        var groupedById = pwList.stream().collect(Collectors.groupingBy(this::getId, LinkedHashMap::new, Collectors.toList()));
        groupedById.forEach((id, ws) -> {
            boolean shouldInsert = ws.get(0).isShouldInsert();
            PaymentWrapper pwLast = ws.get(ws.size() - 1);
            pwLast.setShouldInsert(shouldInsert);
            result.add(pwLast);
        });
    }

    private void setIds(Iterator<Long> iterator, List<PaymentWrapper> pwList) {
        PaymentWrapper wInsert = null;
        PaymentWrapper wUpdate = null;
        for (int i = 0; i < pwList.size(); ++i) {
            PaymentWrapper w = pwList.get(i);
            Long id;
            if (w.isShouldInsert()) {
                wInsert = w;
                wUpdate = null;
                id = iterator.next();
            } else {
                if (wInsert != null) {
                    id = getId(wInsert);
                } else {
                    if (wUpdate != null) {
                        id = getId(wUpdate);
                    } else {
                        wUpdate = w;
                        id = iterator.next();
                    }
                }
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
