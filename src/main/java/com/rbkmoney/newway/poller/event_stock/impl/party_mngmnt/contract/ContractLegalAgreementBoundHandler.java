package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contract;

import com.rbkmoney.damsel.domain.LegalAgreement;
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

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractLegalAgreementBoundHandler extends AbstractClaimChangedHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;

    public ContractLegalAgreementBoundHandler(ContractDao contractDao, ContractAdjustmentDao contractAdjustmentDao, PayoutToolDao payoutToolDao) {
        this.contractDao = contractDao;
        this.contractAdjustmentDao = contractAdjustmentDao;
        this.payoutToolDao = payoutToolDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractEffect() && e.getContractEffect().getEffect().isSetLegalAgreementBound())
                .collect(Collectors.toList());
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            ContractEffectUnit contractEffectUnit = claimEffect.getContractEffect();
            LegalAgreement legalAgreementBound = contractEffectUnit.getEffect().getLegalAgreementBound();
            String contractId = contractEffectUnit.getContractId();
            String partyId = event.getSource().getPartyId();
            log.info("Start contract legal agreement bound handling, eventId={}, partyId={}, contractId={}", eventId, partyId, contractId);
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
            ContractUtil.fillContractLegalAgreementFields(contractSource, legalAgreementBound);
            contractDao.updateNotCurrent(partyId, contractId);
            long cntrctId = contractDao.save(contractSource);

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

            log.info("Contract legal agreement bound has been saved, eventId={}, partyId={}, contractId={}", eventId, partyId, contractId);
        }
    }
}
