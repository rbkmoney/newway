package com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.contract;

import com.rbkmoney.damsel.domain.ContractStatus;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.AbstractClaimChangedHandler;
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
public class ContractStatusChangedHandler extends AbstractClaimChangedHandler {

    private final ContractDao contractDao;
    private final ContractReferenceService contractReferenceService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetContractEffect() && claimEffect.getContractEffect().getEffect().isSetStatusChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i), i);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect e,
                             Integer claimEffectId) {
        ContractEffectUnit contractEffectUnit = e.getContractEffect();
        ContractStatus statusChanged = contractEffectUnit.getEffect().getStatusChanged();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contractSource status changed handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);

        Contract contractSource = contractDao.get(partyId, contractId);
        ContractUtil.resetBaseFields(event, changeId, sequenceId, contractSource, claimEffectId);

        contractSource.setStatus(
                TBaseUtil.unionFieldToEnum(statusChanged, com.rbkmoney.newway.domain.enums.ContractStatus.class));
        if (statusChanged.isSetTerminated()) {
            contractSource.setStatusTerminatedAt(
                    TypeUtil.stringToLocalDateTime(statusChanged.getTerminated().getTerminatedAt()));
        }

        Long contractSourceId = contractSource.getId();

        contractDao.save(contractSource).ifPresentOrElse(
                dbContractId -> {
                    contractDao.updateNotCurrent(contractSourceId);
                    contractReferenceService.updateContractReference(contractSourceId, dbContractId);
                    log.info("Contract status has been saved, sequenceId={}, partyId={}, contractId={}, changeId={}",
                            sequenceId, partyId, contractId, changeId);
                },
                () -> log.info("Contract status duplicated, sequenceId={}, partyId={}, contractId={}, changeId={}",
                        sequenceId, partyId, contractId, changeId)
        );

        log.info("Contract status has been saved, sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
    }
}
