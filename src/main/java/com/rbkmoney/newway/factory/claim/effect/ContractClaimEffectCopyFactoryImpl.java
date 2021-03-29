package com.rbkmoney.newway.factory.claim.effect;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractClaimEffectCopyFactoryImpl implements ClaimEffectCopyFactory<Contract, Integer> {

    @Override
    public Contract create(MachineEvent event, long sequenceId, Integer claimEffectId, Integer id,
                           Contract withdrawalSessionOld) {
        Contract contract = null;
        if (withdrawalSessionOld != null) {
            contract = new Contract(withdrawalSessionOld);
        } else {
            contract = new Contract();
        }
        contract.setId(null);
        contract.setWtime(null);
        contract.setSequenceId((int) sequenceId);
        contract.setChangeId(id);
        contract.setClaimEffectId(claimEffectId);
        contract.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return contract;
    }

}
