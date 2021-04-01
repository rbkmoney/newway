package com.rbkmoney.newway.factory.claim.effect;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import org.springframework.stereotype.Component;

@Component
public class ContractorClaimEffectCopyFactoryImpl implements ClaimEffectCopyFactory<Contractor, Integer> {

    @Override
    public Contractor create(MachineEvent event, long sequenceId, Integer claimEffectId, Integer id,
                             Contractor contractorOld) {
        Contractor contractor = null;
        if (contractorOld != null) {
            contractor = new Contractor(contractorOld);
        } else {
            contractor = new Contractor();
        }
        contractor.setId(null);
        contractor.setWtime(null);
        contractor.setSequenceId((int) sequenceId);
        contractor.setChangeId(id);
        contractor.setClaimEffectId(claimEffectId);
        contractor.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return contractor;
    }

}
