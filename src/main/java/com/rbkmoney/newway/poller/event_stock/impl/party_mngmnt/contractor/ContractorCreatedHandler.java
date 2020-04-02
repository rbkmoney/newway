package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.PartyContractor;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ContractorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class ContractorCreatedHandler extends AbstractClaimChangedHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractorDao contractorDao;
    private final PartyDao partyDao;

    public ContractorCreatedHandler(ContractorDao contractorDao, PartyDao partyDao) {
        this.contractorDao = contractorDao;
        this.partyDao = partyDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractorEffect() && e.getContractorEffect().getEffect().isSetCreated())
                .collect(Collectors.toList());
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            ContractorEffectUnit contractorEffect = claimEffect.getContractorEffect();
            PartyContractor partyContractor = contractorEffect.getEffect().getCreated();
            com.rbkmoney.damsel.domain.Contractor contractorCreated = partyContractor.getContractor();
            String contractorId = contractorEffect.getId();
            String partyId = event.getSource().getPartyId();
            log.info("Start contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
            Party partySource = partyDao.get(partyId);
            if (partySource == null) {
                throw new NotFoundException(String.format("Party not found, partyId='%s'", partyId));
            }
            Contractor contractor = ContractorUtil.convertContractor(eventId, event.getCreatedAt(), partyId, contractorCreated,
                    contractorId, changeId, event.getSequence());
            contractor.setIdentificationalLevel(partyContractor.getStatus().name());
            contractor.setClaimEffectId(i);
            contractorDao.save(contractor);
            log.info("Contract contractor has been saved, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);

        }
    }


}
