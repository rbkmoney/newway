package com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.contract;

import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PayoutToolInfoChanged;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.factory.claim.effect.ClaimEffectCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.service.ContractReferenceService;
import com.rbkmoney.newway.util.ContractUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractPayoutToolInfoChangedHandler extends AbstractClaimChangedHandler {

    private final ContractDao contractDao;
    private final PayoutToolDao payoutToolDao;
    private final ContractReferenceService contractReferenceService;
    private final ClaimEffectCopyFactory<Contract, Integer> claimEffectCopyFactory;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetContractEffect()
                    && claimEffect.getContractEffect().getEffect().isSetPayoutToolInfoChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i), i);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect claimEffect,
                             Integer claimEffectId) {
        ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
        String contractId = contractEffectUnit.getContractId();
        String partyId = event.getSourceId();
        log.info("Start contract payout tool info changed handling, " +
                        "sequenceId={}, partyId={}, contractId={}, changeId={}",
                sequenceId, partyId, contractId, changeId);
        Contract contractSourceOld = contractDao.get(partyId, contractId);
        Contract contractNew =
                claimEffectCopyFactory.create(event, sequenceId, claimEffectId, changeId, contractSourceOld);

        contractDao.save(contractNew).ifPresentOrElse(
                dbContractId -> {
                    Long oldId = contractSourceOld.getId();
                    contractDao.updateNotCurrent(oldId);
                    contractReferenceService.updateAdjustments(oldId, dbContractId);
                    updatePayoutTools(contractEffectUnit, oldId, dbContractId);
                    log.info("Contract payout tool info changed has been saved, " +
                                    "sequenceId={}, partyId={}, contractId={}, changeId={}",
                            sequenceId, partyId, contractId, changeId);
                },
                () -> log.info("Contract payout tool info changed duplicated, " +
                                "sequenceId={}, partyId={}, contractId={}, changeId={}",
                        sequenceId, partyId, contractId, changeId)
        );
    }

    private void updatePayoutTools(ContractEffectUnit contractEffectUnit, Long oldId, Long dbContractId) {
        List<PayoutTool> currentPayoutTools = payoutToolDao.getByCntrctId(oldId);
        currentPayoutTools.forEach(pt -> {
            pt.setId(null);
            pt.setCntrctId(dbContractId);
        });
        PayoutToolInfoChanged payoutToolInfoChanged =
                contractEffectUnit.getEffect().getPayoutToolInfoChanged();
        Optional<PayoutTool> payoutToolForChangeOptional = currentPayoutTools.stream()
                .filter(p -> p.getPayoutToolId().equals(payoutToolInfoChanged.getPayoutToolId()))
                .findAny();
        List<PayoutTool> modifiedPayoutTools;
        if (payoutToolForChangeOptional.isPresent()) {
            PayoutTool payoutToolForChange = payoutToolForChangeOptional.get();
            PayoutTool newPayoutTool = ContractUtil.buildPayoutTool(dbContractId,
                    payoutToolInfoChanged.getPayoutToolId(),
                    payoutToolForChange.getCreatedAt(),
                    payoutToolForChange.getCurrencyCode(),
                    payoutToolInfoChanged.getInfo()
            );
            modifiedPayoutTools = Stream.concat(
                    Stream.of(newPayoutTool),
                    currentPayoutTools.stream()
                            .filter(p -> !p.getPayoutToolId().equals(payoutToolInfoChanged.getPayoutToolId()))
            ).collect(Collectors.toList());
        } else {
            modifiedPayoutTools = currentPayoutTools;
        }
        payoutToolDao.save(modifiedPayoutTools);
    }
}
