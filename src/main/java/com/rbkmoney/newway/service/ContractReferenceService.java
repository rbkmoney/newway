package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractReferenceService {

    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateContractReference(Long contractSourceId, Long contractId) {
        List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
        adjustments.forEach(a -> {
            a.setId(null);
            a.setCntrctId(contractId);
        });
        contractAdjustmentDao.save(adjustments);

        List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
        payoutTools.forEach(pt -> {
            pt.setId(null);
            pt.setCntrctId(contractId);
        });
        payoutToolDao.save(payoutTools);
    }

}
