package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.InternationalLegalEntity;
import com.rbkmoney.damsel.domain.RussianLegalEntity;
import com.rbkmoney.damsel.domain.RussianPrivateEntity;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.enums.ContractorType;
import com.rbkmoney.newway.domain.enums.LegalEntity;
import com.rbkmoney.newway.domain.enums.PrivateEntity;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;

public class ContractorUtil {
    public static Contractor convertContractor(long sequenceId, String eventCreatedAt, String partyId,
                                               com.rbkmoney.damsel.domain.Contractor contractorSource,
                                               String contractorId, Integer changeId, Integer claimEffectId) {
        Contractor contractor = new Contractor();
        contractor.setSequenceId((int) sequenceId);
        contractor.setChangeId(changeId);
        contractor.setClaimEffectId(claimEffectId);
        contractor.setEventCreatedAt(TypeUtil.stringToLocalDateTime(eventCreatedAt));
        contractor.setPartyId(partyId);
        contractor.setContractorId(contractorId);
        contractor.setType(TBaseUtil.unionFieldToEnum(contractorSource, ContractorType.class));
        if (contractorSource.isSetRegisteredUser()) {
            contractor.setRegisteredUserEmail(contractorSource.getRegisteredUser().getEmail());
        } else if (contractorSource.isSetLegalEntity()) {
            contractor.setLegalEntity(TBaseUtil.unionFieldToEnum(contractorSource.getLegalEntity(), LegalEntity.class));
            if (contractorSource.getLegalEntity().isSetRussianLegalEntity()) {
                RussianLegalEntity russianLegalEntity = contractorSource.getLegalEntity().getRussianLegalEntity();
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
            } else if (contractorSource.getLegalEntity().isSetInternationalLegalEntity()) {
                InternationalLegalEntity internationalLegalEntity = contractorSource.getLegalEntity().getInternationalLegalEntity();
                contractor.setInternationalLegalEntityLegalName(internationalLegalEntity.getLegalName());
                contractor.setInternationalLegalEntityTradingName(internationalLegalEntity.getTradingName());
                contractor.setInternationalLegalEntityRegisteredAddress(internationalLegalEntity.getRegisteredAddress());
                contractor.setInternationalLegalEntityActualAddress(internationalLegalEntity.getActualAddress());
                contractor.setInternationalLegalEntityRegisteredNumber(internationalLegalEntity.getRegisteredNumber());
            }
        } else if (contractorSource.isSetPrivateEntity()) {
            contractor.setPrivateEntity(TBaseUtil.unionFieldToEnum(contractorSource.getPrivateEntity(), PrivateEntity.class));
            if (contractorSource.getPrivateEntity().isSetRussianPrivateEntity()) {
                RussianPrivateEntity russianPrivateEntity = contractorSource.getPrivateEntity().getRussianPrivateEntity();
                contractor.setRussianPrivateEntityFirstName(russianPrivateEntity.getFirstName());
                contractor.setRussianPrivateEntitySecondName(russianPrivateEntity.getSecondName());
                contractor.setRussianPrivateEntityMiddleName(russianPrivateEntity.getMiddleName());
                contractor.setRussianPrivateEntityPhoneNumber(russianPrivateEntity.getContactInfo().getPhoneNumber());
                contractor.setRussianPrivateEntityEmail(russianPrivateEntity.getContactInfo().getEmail());
            }
        }
        return contractor;
    }


    public static void resetBaseFields(MachineEvent event, long sequenceId, Contractor contractorSource, Integer claimEffectId) {
        contractorSource.setId(null);
        contractorSource.setWtime(null);
        contractorSource.setClaimEffectId(claimEffectId);
        contractorSource.setSequenceId((int) sequenceId);
        contractorSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }
}
