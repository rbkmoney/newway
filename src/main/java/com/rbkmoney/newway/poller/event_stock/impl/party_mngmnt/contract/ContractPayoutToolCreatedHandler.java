package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contract;

import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.service.ContractReferenceService;
import com.rbkmoney.newway.util.ContractUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractPayoutToolCreatedHandler extends AbstractClaimChangedHandler {

    private final ContractDao contractDao;
    private final ContractReferenceService contractReferenceService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetContractEffect() && claimEffect.getContractEffect().getEffect().isSetPayoutToolCreated()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i), i);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect, Integer claimEffectId) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        com.rbkmoney.damsel.domain.PayoutTool payoutToolCreated = contractEffectUnit.getEffect().getPayoutToolCreated();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract payout tool created handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
        Contract contractSource = contractDao.get(partyId, contractId);
        Long contractSourceId = contractSource.getId();
        ContractUtil.resetBaseFields(event, changeId, sequenceId, contractSource, claimEffectId);

        contractDao.save(contractSource).ifPresentOrElse(
                dbContractId -> {
                    contractDao.updateNotCurrent(contractSourceId);
                    contractReferenceService.updateContractReference(contractSourceId, dbContractId);
                    log.info("Contract contract payout tool created has been saved, sequenceId={}, partyId={}, contractId={}, changeId={}",
                            sequenceId, partyId, contractId, changeId);
                },
                () -> log.info("Contract contract payout tool created duplicated, sequenceId={}, partyId={}, contractId={}, changeId={}",
                        sequenceId, partyId, contractId, changeId)
        );
    }
}
