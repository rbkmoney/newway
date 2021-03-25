package com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.shop;

import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ShopUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDetailsChangedHandler extends AbstractClaimChangedHandler {

    private final ShopDao shopDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects();
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            if (claimEffect.isSetShopEffect() && claimEffect.getShopEffect().getEffect().isSetDetailsChanged()) {
                handleEvent(event, changeId, sequenceId, claimEffects.get(i), i);
            }
        }
    }

    private void handleEvent(MachineEvent event, Integer changeId, long sequenceId, ClaimEffect e,
                             Integer claimEffectId) {
        ShopEffectUnit shopEffect = e.getShopEffect();
        ShopDetails detailsChanged = shopEffect.getEffect().getDetailsChanged();
        String shopId = shopEffect.getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop detailsChanged handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        Shop shopSource = shopDao.get(partyId, shopId);
        ShopUtil.resetBaseFields(event, changeId, sequenceId, shopSource, claimEffectId);

        shopSource.setDetailsName(detailsChanged.getName());
        shopSource.setDetailsDescription(detailsChanged.getDescription());

        Long oldEventId = shopSource.getId();
        shopDao.saveWithUpdateCurrent(shopSource, oldEventId, "detailsChanged");
    }
}
