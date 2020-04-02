package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.ContractorIdentificationLevel;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractorIdentificationalLevelChangedHandler extends AbstractClaimChangedHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractorDao contractorDao;

    public ContractorIdentificationalLevelChangedHandler(ContractorDao contractorDao) {
        this.contractorDao = contractorDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractorEffect() && e.getContractorEffect().getEffect().isSetIdentificationLevelChanged())
                .collect(Collectors.toList());
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            ContractorEffectUnit contractorEffect = claimEffect.getContractorEffect();
            ContractorIdentificationLevel identificationLevelChanged = contractorEffect.getEffect().getIdentificationLevelChanged();
            String contractorId = contractorEffect.getId();
            String partyId = event.getSource().getPartyId();
            log.info("Start identificational level changed handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
            Contractor contractorSource = contractorDao.get(partyId, contractorId);
            if (contractorSource == null) {
                throw new NotFoundException(String.format("Contractor not found, contractorId='%s'", contractorId));
            }
            contractorSource.setId(null);
            contractorSource.setRevision(null);
            contractorSource.setWtime(null);
            contractorSource.setEventId(eventId);
            contractorSource.setSequenceId(event.getSequence());
            contractorSource.setChangeId(changeId);
            contractorSource.setClaimEffectId(i);
            contractorSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractorSource.setIdentificationalLevel(identificationLevelChanged.name());
            contractorDao.updateNotCurrent(partyId, contractorId);
            contractorDao.save(contractorSource);
            log.info("Contract identificational level has been saved, eventId={}, contractorId={}", eventId, contractorId);
        }
    }
}
