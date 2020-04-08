package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ShopUtil;
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
public class ShopCreatedHandler extends AbstractClaimChangedHandler {

    private final ShopDao shopDao;
    private final PartyDao partyDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetShopEffect() && e.getShopEffect().getEffect().isSetCreated())
                .forEach(e -> handleEvent(event, changeId, e));
    }

    private void handleEvent(MachineEvent event, Integer changeId, ClaimEffect e) {
        long sequenceId = event.getEventId();
        ShopEffectUnit shopEffect = e.getShopEffect();
        Shop shopCreated = shopEffect.getEffect().getCreated();
        String shopId = shopEffect.getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop created handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);

        partyDao.get(partyId); //check party is exist
        com.rbkmoney.newway.domain.tables.pojos.Shop shop = createShop(event, changeId, sequenceId, shopCreated, shopId, partyId);

        shopDao.save(shop).ifPresentOrElse(
                aLong -> log.info("Shop has been saved, sequenceId={}, ppartyId={}, shopId={}, changeId={}", sequenceId, partyId, shopId, changeId),
                () -> log.info("Shop create duplicated, sequenceId={}, partyId={}, shopId={}, changeId={}", sequenceId, partyId, shopId, changeId));
    }

    private com.rbkmoney.newway.domain.tables.pojos.Shop createShop(MachineEvent event, Integer changeId, long sequenceId, Shop shopCreated, String shopId, String partyId) {
        com.rbkmoney.newway.domain.tables.pojos.Shop shop = new com.rbkmoney.newway.domain.tables.pojos.Shop();
        shop.setSequenceId((int) sequenceId);
        shop.setChangeId(changeId);
        shop.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        shop.setShopId(shopId);
        shop.setPartyId(partyId);
        shop.setCreatedAt(TypeUtil.stringToLocalDateTime(shopCreated.getCreatedAt()));
        shop.setBlocking(TBaseUtil.unionFieldToEnum(shopCreated.getBlocking(), com.rbkmoney.newway.domain.enums.Blocking.class));
        if (shopCreated.getBlocking().isSetUnblocked()) {
            shop.setBlockingUnblockedReason(shopCreated.getBlocking().getUnblocked().getReason());
            shop.setBlockingUnblockedSince(TypeUtil.stringToLocalDateTime(shopCreated.getBlocking().getUnblocked().getSince()));
        } else if (shopCreated.getBlocking().isSetBlocked()) {
            shop.setBlockingBlockedReason(shopCreated.getBlocking().getBlocked().getReason());
            shop.setBlockingBlockedSince(TypeUtil.stringToLocalDateTime(shopCreated.getBlocking().getBlocked().getSince()));
        }
        shop.setSuspension(TBaseUtil.unionFieldToEnum(shopCreated.getSuspension(), com.rbkmoney.newway.domain.enums.Suspension.class));
        if (shopCreated.getSuspension().isSetActive()) {
            shop.setSuspensionActiveSince(TypeUtil.stringToLocalDateTime(shopCreated.getSuspension().getActive().getSince()));
        } else if (shopCreated.getSuspension().isSetSuspended()) {
            shop.setSuspensionSuspendedSince(TypeUtil.stringToLocalDateTime(shopCreated.getSuspension().getSuspended().getSince()));
        }
        shop.setDetailsName(shopCreated.getDetails().getName());
        shop.setDetailsDescription(shopCreated.getDetails().getDescription());
        if (shopCreated.getLocation().isSetUrl()) {
            shop.setLocationUrl(shopCreated.getLocation().getUrl());
        } else {
            throw new IllegalArgumentException("Illegal shop location " + shopCreated.getLocation());
        }
        shop.setCategoryId(shopCreated.getCategory().getId());
        if (shopCreated.isSetAccount()) {
            ShopUtil.fillShopAccount(shop, shopCreated.getAccount());
        }
        shop.setContractId(shopCreated.getContractId());
        shop.setPayoutToolId(shopCreated.getPayoutToolId());
        if (shopCreated.isSetPayoutSchedule()) {
            shop.setPayoutScheduleId(shopCreated.getPayoutSchedule().getId());
        }
        return shop;
    }
}
