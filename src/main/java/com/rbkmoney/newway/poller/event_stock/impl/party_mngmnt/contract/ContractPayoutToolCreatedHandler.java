package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contract;

import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ContractUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractPayoutToolCreatedHandler extends AbstractClaimChangedHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;

    public ContractPayoutToolCreatedHandler(ContractDao contractDao, ContractAdjustmentDao contractAdjustmentDao, PayoutToolDao payoutToolDao) {
        this.contractDao = contractDao;
        this.contractAdjustmentDao = contractAdjustmentDao;
        this.payoutToolDao = payoutToolDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractEffect() && e.getContractEffect().getEffect().isSetPayoutToolCreated())
                .collect(Collectors.toList());
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
            com.rbkmoney.damsel.domain.PayoutTool payoutToolCreated = contractEffectUnit.getEffect().getPayoutToolCreated();
            String contractId = contractEffectUnit.getContractId();
            String partyId = event.getSource().getPartyId();
            log.info("Start contract payouttool created handling, eventId={}, partyId={}, contractId={}", eventId, partyId, contractId);
            Contract contractSource = contractDao.get(partyId, contractId);
            if (contractSource == null) {
                throw new NotFoundException(String.format("Contract not found, contractId='%s'", contractId));
            }
            Long contractSourceId = contractSource.getId();
            contractSource.setId(null);
            contractSource.setRevision(-1L);
            contractSource.setWtime(null);
            contractSource.setEventId(eventId);
            contractSource.setSequenceId(event.getSequence());
            contractSource.setChangeId(changeId);
            contractSource.setClaimEffectId(i);
            contractSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractDao.updateNotCurrent(partyId, contractId);
            long cntrctId = contractDao.save(contractSource);

            List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
            adjustments.forEach(a -> {
                a.setId(null);
                a.setCntrctId(cntrctId);
            });
            contractAdjustmentDao.save(adjustments);

            List<PayoutTool> payoutTools = new ArrayList<>(payoutToolDao.getByCntrctId(contractSourceId));
            payoutTools.forEach(pt -> {
                pt.setId(null);
                pt.setCntrctId(cntrctId);
            });
            payoutTools.add(ContractUtil.convertPayoutTool(payoutToolCreated, cntrctId));
            payoutToolDao.save(payoutTools);

            log.info("Contract payouttool has been saved, eventId={}, partyId={}, contractId={}", eventId, partyId, contractId);
        }
    }
}
