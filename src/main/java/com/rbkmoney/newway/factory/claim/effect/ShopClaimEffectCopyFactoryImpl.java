package com.rbkmoney.newway.factory.claim.effect;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import org.springframework.stereotype.Component;

@Component
public class ShopClaimEffectCopyFactoryImpl implements ClaimEffectCopyFactory<Shop, Integer> {

    @Override
    public Shop create(MachineEvent event, long sequenceId, Integer claimEffectId, Integer id,
                       Shop old) {
        Shop shop = null;
        if (old != null) {
            shop = new Shop(old);
        } else {
            shop = new Shop();
        }
        shop.setId(null);
        shop.setWtime(null);
        shop.setSequenceId((int) sequenceId);
        shop.setChangeId(id);
        shop.setClaimEffectId(claimEffectId);
        shop.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        return shop;
    }

}
