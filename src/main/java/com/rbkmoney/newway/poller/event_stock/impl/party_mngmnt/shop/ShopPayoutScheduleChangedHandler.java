package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.ScheduleChanged;
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ShopPayoutScheduleChangedHandler extends AbstractClaimChangedHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ShopDao shopDao;

    public ShopPayoutScheduleChangedHandler(ShopDao shopDao) {
        this.shopDao = shopDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetShopEffect() && e.getShopEffect().getEffect().isSetPayoutScheduleChanged()).forEach(e -> {
            ShopEffectUnit shopEffect = e.getShopEffect();
            ScheduleChanged payoutScheduleChanged = shopEffect.getEffect().getPayoutScheduleChanged();
            String shopId = shopEffect.getShopId();
            String partyId = event.getSource().getPartyId();
            log.info("Start shop payoutScheduleChanged handling, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
            Shop shopSource = shopDao.get(partyId, shopId);
            if (shopSource == null) {
                throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
            }
            shopSource.setId(null);
            shopSource.setWtime(null);
            shopSource.setEventId(eventId);
            shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            if (payoutScheduleChanged.isSetSchedule()) {
                shopSource.setPayoutScheduleId(payoutScheduleChanged.getSchedule().getId());
            } else {
                shopSource.setPayoutScheduleId(null);
            }
            shopDao.updateNotCurrent(partyId, shopId);
            shopDao.save(shopSource);
            log.info("Shop payoutScheduleChanged has been saved, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
        });
    }
}
