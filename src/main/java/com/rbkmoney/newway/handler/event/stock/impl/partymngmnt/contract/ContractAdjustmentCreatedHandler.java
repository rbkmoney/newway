package com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.contract;

import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.factory.claim.effect.ClaimEffectCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ContractUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractAdjustmentCreatedHandler extends AbstractClaimChangedHandler {

    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;
    private final ClaimEffectCopyFactory<Contract, Integer> claimEffectCopyFactory;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetContractEffect()
                    && claimEffect.getContractEffect().getEffect().isSetAdjustmentCreated()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i), i);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect,
                             Integer claimEffectId) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        com.rbkmoney.damsel.domain.ContractAdjustment adjustmentCreated =
                contractEffectUnit.getEffect().getAdjustmentCreated();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract adjustment created handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);

        Contract contractSourceOld = contractDao.get(partyId, contractId);
        Contract contractNew =
                claimEffectCopyFactory.create(event, sequenceId, changeId, claimEffectId, contractSourceOld);

        contractDao.save(contractNew).ifPresentOrElse(
                cntrctId -> {
                    Long contractSourceOldId = contractSourceOld.getId();
                    contractDao.updateNotCurrent(contractSourceOldId);
                    updateContractReference(adjustmentCreated, contractSourceOldId, cntrctId);
                    log.info(
                            "Contract adjustment has been saved, sequenceId={}, partyId={}, contractId={}, changeId={}",
                            sequenceId, partyId, contractId, changeId);
                },
                () -> log.info("Contract adjustment duplicated, sequenceId={}, partyId={}, contractId={}, changeId={}",
                        sequenceId, partyId, contractId, changeId)
        );
    }

    private void updateContractReference(com.rbkmoney.damsel.domain.ContractAdjustment adjustmentCreated,
                                         Long contractSourceId, Long cntrctId) {
        List<ContractAdjustment> adjustments = new ArrayList<>(contractAdjustmentDao.getByCntrctId(contractSourceId));
        adjustments.forEach(a -> {
            a.setId(null);
            a.setCntrctId(cntrctId);
        });
        adjustments.add(ContractUtil.convertContractAdjustment(adjustmentCreated, cntrctId));
        contractAdjustmentDao.save(adjustments);

        List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
        payoutTools.forEach(pt -> {
            pt.setId(null);
            pt.setCntrctId(cntrctId);
        });
        payoutToolDao.save(payoutTools);
    }

}
