package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.ContractorIdentificationLevel;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractorIdentificationalLevelChangedHandler extends AbstractClaimChangedHandler {

    private final ContractorDao contractorDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event) {
        long eventId = event.getEventId();
        getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractorEffect() && e.getContractorEffect().getEffect().isSetIdentificationLevelChanged()).forEach(e -> {
            ContractorEffectUnit contractorEffect = e.getContractorEffect();
            ContractorIdentificationLevel identificationLevelChanged = contractorEffect.getEffect().getIdentificationLevelChanged();
            String contractorId = contractorEffect.getId();
            String partyId = event.getSourceId();
            log.info("Start identificational level changed handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
            Contractor contractorSource = contractorDao.get(partyId, contractorId);
            if (contractorSource == null) {
                throw new NotFoundException(String.format("Contractor not found, contractorId='%s'", contractorId));
            }
            contractorSource.setId(null);
            contractorSource.setRevision(null);
            contractorSource.setWtime(null);
            contractorSource.setEventId(eventId);
            contractorSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractorSource.setIdentificationalLevel(identificationLevelChanged.name());
            contractorDao.updateNotCurrent(partyId, contractorId);
            contractorDao.save(contractorSource);
            log.info("Contract identificational level has been saved, eventId={}, contractorId={}", eventId, contractorId);
        });
    }
}
