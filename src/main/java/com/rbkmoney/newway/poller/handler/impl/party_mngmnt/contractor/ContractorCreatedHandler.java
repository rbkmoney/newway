package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.contractor;

import com.rbkmoney.damsel.domain.InternationalLegalEntity;
import com.rbkmoney.damsel.domain.PartyContractor;
import com.rbkmoney.damsel.domain.RussianLegalEntity;
import com.rbkmoney.damsel.domain.RussianPrivateEntity;
import com.rbkmoney.damsel.payment_processing.ContractorEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.domain.enums.Contractortype;
import com.rbkmoney.newway.domain.enums.Legalentity;
import com.rbkmoney.newway.domain.enums.Privateentity;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContractorCreatedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractorDao contractorDao;

    private final Filter filter;

    public ContractorCreatedHandler(ContractorDao contractorDao) {
        this.contractorDao = contractorDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "claim_created.status.accepted",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        change.getClaimCreated().getStatus().getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractorEffect() && e.getContractorEffect().getEffect().isSetCreated()).forEach(e -> {
            ContractorEffectUnit contractorEffect = e.getContractorEffect();
            PartyContractor partyContractor = contractorEffect.getEffect().getCreated();
            com.rbkmoney.damsel.domain.Contractor contractorCreated = partyContractor.getContractor();
            String contractorId = contractorEffect.getId();
            String partyId = event.getSource().getPartyId();
            log.info("Start contractor created handling, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);
            Contractor contractor = new Contractor();
            contractor.setEventId(eventId);
            contractor.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contractor.setPartyId(partyId);
            contractor.setContractorId(contractorId);
            contractor.setIdentificationalLevel(partyContractor.getStatus().name());
            Contractortype contractortype = TypeUtil.toEnumField(contractorCreated.getSetField().getFieldName(), Contractortype.class);
            if (contractortype == null) {
                throw new IllegalArgumentException("Illegal contractorType: " + contractorCreated);
            }
            contractor.setType(contractortype);
            if (contractorCreated.isSetRegisteredUser()) {
                contractor.setRegisteredUserEmail(contractorCreated.getRegisteredUser().getEmail());
            } else if (contractorCreated.isSetLegalEntity()) {
                Legalentity legalEntity = TypeUtil.toEnumField(contractorCreated.getLegalEntity().getSetField().getFieldName(), Legalentity.class);
                if (legalEntity == null) {
                    throw new IllegalArgumentException("Unknown legal entity: " + contractor.getLegalEntity());
                }
                contractor.setLegalEntity(legalEntity);
                if (contractorCreated.getLegalEntity().isSetRussianLegalEntity()) {
                    RussianLegalEntity russianLegalEntity = contractorCreated.getLegalEntity().getRussianLegalEntity();
                    contractor.setRussianLegalEntityRegisteredName(russianLegalEntity.getRegisteredName());
                    contractor.setRussianLegalEntityRegisteredNumber(russianLegalEntity.getRegisteredNumber());
                    contractor.setRussianLegalEntityInn(russianLegalEntity.getInn());
                    contractor.setRussianLegalEntityActualAddress(russianLegalEntity.getActualAddress());
                    contractor.setRussianLegalEntityPostAddress(russianLegalEntity.getPostAddress());
                    contractor.setRussianLegalEntityRepresentativePosition(russianLegalEntity.getRepresentativePosition());
                    contractor.setRussianLegalEntityRepresentativeFullName(russianLegalEntity.getRepresentativeFullName());
                    contractor.setRussianLegalEntityRepresentativeDocument(russianLegalEntity.getRepresentativeDocument());
                    contractor.setRussianLegalEntityRussianBankAccount(russianLegalEntity.getRussianBankAccount().getAccount());
                    contractor.setRussianLegalEntityRussianBankName(russianLegalEntity.getRussianBankAccount().getBankName());
                    contractor.setRussianLegalEntityRussianBankPostAccount(russianLegalEntity.getRussianBankAccount().getBankPostAccount());
                    contractor.setRussianLegalEntityRussianBankBik(russianLegalEntity.getRussianBankAccount().getBankBik());
                } else if (contractorCreated.getLegalEntity().isSetInternationalLegalEntity()) {
                    InternationalLegalEntity internationalLegalEntity = contractorCreated.getLegalEntity().getInternationalLegalEntity();
                    contractor.setInternationalLegalEntityLegalName(internationalLegalEntity.getLegalName());
                    contractor.setInternationalLegalEntityTradingName(internationalLegalEntity.getTradingName());
                    contractor.setInternationalLegalEntityRegisteredAddress(internationalLegalEntity.getRegisteredAddress());
                    contractor.setInternationalLegalEntityActualAddress(internationalLegalEntity.getActualAddress());
                    contractor.setInternationalLegalEntityRegisteredNumber(internationalLegalEntity.getRegisteredNumber());
                }
            } else if (contractorCreated.isSetPrivateEntity()) {
                Privateentity privateEntity = TypeUtil.toEnumField(contractorCreated.getPrivateEntity().getSetField().getFieldName(), Privateentity.class);
                if (privateEntity == null) {
                    throw new IllegalArgumentException("Illegal private entity: " + contractor.getPrivateEntity());
                }
                if (contractorCreated.getPrivateEntity().isSetRussianPrivateEntity()) {
                    RussianPrivateEntity russianPrivateEntity = contractorCreated.getPrivateEntity().getRussianPrivateEntity();
                    contractor.setRussianPrivateEntityFirstName(russianPrivateEntity.getFirstName());
                    contractor.setRussianPrivateEntitySecondName(russianPrivateEntity.getSecondName());
                    contractor.setRussianPrivateEntityMiddleName(russianPrivateEntity.getMiddleName());
                    contractor.setRussianPrivateEntityPhoneNumber(russianPrivateEntity.getContactInfo().getPhoneNumber());
                    contractor.setRussianPrivateEntityEmail(russianPrivateEntity.getContactInfo().getEmail());
                }
            }
            contractorDao.save(contractor);
            log.info("Contract contractor has been saved, eventId={}, partyId={}, contractorId={}", eventId, partyId, contractorId);

        });
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
