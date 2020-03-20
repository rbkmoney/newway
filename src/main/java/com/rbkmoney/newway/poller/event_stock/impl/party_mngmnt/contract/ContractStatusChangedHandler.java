package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contract;

import com.rbkmoney.damsel.domain.ContractStatus;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
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
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractEffect() && e.getContractEffect().getEffect().isSetStatusChanged()).forEach(e -> {
            ContractEffectUnit contractEffectUnit = e.getContractEffect();
            ContractStatus statusChanged = contractEffectUnit.getEffect().getStatusChanged();
            String contractId = contractEffectUnit.getContractId();
            String partyId = event.getSourceId();
            log.info("Start contractSource status changed handling, sequenceId={}, partyId={}, contractId={}, changeId={}",
                    sequenceId, partyId, contractId, changeId);
            Contract contractSource = contractDao.get(partyId, contractId);
            if (contractSource == null) {
                throw new NotFoundException(String.format("Contract not found, contractId='%s'", contractId));
            }
            Long contractSourceId = contractSource.getId();
            contractSource.setId(null);
            contractSource.setRevision(null);
            contractSource.setWtime(null);
            contractSource.setSequenceId(sequenceId);
            contractSource.setChangeId(changeId);
            contractSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractSource.setStatus(TBaseUtil.unionFieldToEnum(statusChanged, com.rbkmoney.newway.domain.enums.ContractStatus.class));
            if (statusChanged.isSetTerminated()) {
                contractSource.setStatusTerminatedAt(TypeUtil.stringToLocalDateTime(statusChanged.getTerminated().getTerminatedAt()));
            }
            long cntrctId = contractDao.save(contractSource);
            contractDao.switchCurrent(partyId, contractId);

            List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
            adjustments.forEach(a -> {
                a.setId(null);
                a.setCntrctId(cntrctId);
            });
            contractAdjustmentDao.save(adjustments);

            List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
            payoutTools.forEach(pt -> {
                pt.setId(null);
                pt.setCntrctId(cntrctId);
            });
            payoutToolDao.save(payoutTools);

            log.info("Contract status has been saved, sequenceId={}, partyId={}, contractId={}, changeId={}",
                    sequenceId, partyId, contractId, changeId);
        });
    }
}
