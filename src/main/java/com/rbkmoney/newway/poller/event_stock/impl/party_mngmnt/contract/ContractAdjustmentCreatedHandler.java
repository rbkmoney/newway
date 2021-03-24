package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contract;

import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetContractEffect() && claimEffect.getContractEffect().getEffect().isSetAdjustmentCreated()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i), i);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect, Integer claimEffectId) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        com.rbkmoney.damsel.domain.ContractAdjustment adjustmentCreated = contractEffectUnit.getEffect().getAdjustmentCreated();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract adjustment created handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);

        Contract contractSource = contractDao.get(partyId, contractId);
        Long contractSourceId = contractSource.getId();
        ContractUtil.resetBaseFields(event, changeId, sequenceId, contractSource, claimEffectId);

        contractDao.save(contractSource).ifPresentOrElse(
                cntrctId -> {
                    contractDao.updateNotCurrent(contractSourceId);
                    updateContractReference(adjustmentCreated, contractSourceId, cntrctId);
                    log.info("Contract adjustment has been saved, sequenceId={}, partyId={}, contractId={}, changeId={}",
                            sequenceId, partyId, contractId, changeId);
                },
                () -> log.info("Contract adjustment duplicated, sequenceId={}, partyId={}, contractId={}, changeId={}",
                        sequenceId, partyId, contractId, changeId)
        );
    }

    private void updateContractReference(com.rbkmoney.damsel.domain.ContractAdjustment adjustmentCreated, Long contractSourceId, Long cntrctId) {
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
