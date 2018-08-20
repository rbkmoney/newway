package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.ContractorIdentificationLevel;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContractorIdentificationalLevelChangedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractorDao contractorDao;

    private final Filter filter;

    public ContractorIdentificationalLevelChangedHandler(ContractorDao contractorDao) {
        this.contractorDao = contractorDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "claim_created.status.accepted",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        change.getClaimCreated().getStatus().getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractorEffect() && e.getContractorEffect().getEffect().isSetIdentificationLevelChanged()).forEach(e -> {
            ContractorEffectUnit contractorEffect = e.getContractorEffect();
            ContractorIdentificationLevel identificationLevelChanged = contractorEffect.getEffect().getIdentificationLevelChanged();
            String contractorId = contractorEffect.getId();
            String partyId = event.getSource().getPartyId();
            log.info("Start identificational level changed handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
            Contractor contractorSource = contractorDao.get(contractorId);
            if (contractorSource == null) {
                throw new NotFoundException(String.format("Contractor not found, contractorId='%s'", contractorId));
            }
            contractorSource.setId(null);
            contractorSource.setEventId(eventId);
            contractorSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractorSource.setIdentificationalLevel(identificationLevelChanged.name());
            contractorDao.update(contractorId);
            contractorDao.save(contractorSource);
            log.info("Contract identificational level has been saved, eventId={}, contractorId={}", eventId, contractorId);
        });
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
