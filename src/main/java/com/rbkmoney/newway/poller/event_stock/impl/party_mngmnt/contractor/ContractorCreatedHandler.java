package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.PartyContractor;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ContractorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ContractorCreatedHandler extends AbstractClaimChangedHandler {

    private final ContractorDao contractorDao;
    private final PartyDao partyDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event) {
        long eventId = event.getEventId();
        getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractorEffect() && e.getContractorEffect().getEffect().isSetCreated()).forEach(e -> {
            ContractorEffectUnit contractorEffect = e.getContractorEffect();
            PartyContractor partyContractor = contractorEffect.getEffect().getCreated();
            com.rbkmoney.damsel.domain.Contractor contractorCreated = partyContractor.getContractor();
            String contractorId = contractorEffect.getId();
            String partyId = event.getSourceId();
            log.info("Start contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
            Party partySource = partyDao.get(partyId);
            if (partySource == null) {
                throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
            }
            Contractor contractor = ContractorUtil.convertContractor(eventId, event.getCreatedAt(), partyId, contractorCreated, contractorId);
            contractor.setIdentificationalLevel(partyContractor.getStatus().name());
            contractorDao.save(contractor);
            log.info("Contract contractor has been saved, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);

        });
    }


}
