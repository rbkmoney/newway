package com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.shop;

import com.rbkmoney.damsel.domain.ShopDetails;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.factory.claim.effect.ClaimEffectCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.AbstractClaimChangedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("VariableDeclarationUsageDistance")
public class ShopDetailsChangedHandler extends AbstractClaimChangedHandler {

    private final ShopDao shopDao;
    private final ClaimEffectCopyFactory<Shop, Integer> claimEffectCopyFactory;

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

        final Shop shopOld = shopDao.get(partyId, shopId);
        Shop shopNew = claimEffectCopyFactory.create(event, sequenceId, claimEffectId, changeId, shopOld);

        shopNew.setDetailsName(detailsChanged.getName());
        shopNew.setDetailsDescription(detailsChanged.getDescription());

        shopDao.saveWithUpdateCurrent(shopOld, shopOld.getId(), "detailsChanged");
    }
}
