package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.ScheduleChanged;
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShopPayoutSheduleChangedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ShopDao shopDao;
    private final Filter filter;

    public ShopPayoutSheduleChangedHandler(ShopDao shopDao) {
        this.shopDao = shopDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "claim_created.status.accepted",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        change.getClaimCreated().getStatus().getAccepted().getEffects().stream()
                .filter(e -> e.isSetShopEffect() && e.getShopEffect().getEffect().isSetPayoutScheduleChanged()).forEach(e -> {
            ShopEffectUnit shopEffect = e.getShopEffect();
            ScheduleChanged payoutScheduleChanged = shopEffect.getEffect().getPayoutScheduleChanged();
            String shopId = shopEffect.getShopId();
            String partyId = event.getSource().getPartyId();
            log.info("Start shop payoutScheduleChanged handling, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
            Shop shopSource = shopDao.get(shopId);
            if (shopSource == null) {
                throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
            }
            shopSource.setId(null);
            shopSource.setEventId(eventId);
            shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            if (payoutScheduleChanged.isSetSchedule()) {
                shopSource.setPayoutScheduleId(payoutScheduleChanged.getSchedule().getId());
            }
            shopDao.update(shopId);
            shopDao.save(shopSource);
            log.info("Shop payoutScheduleChanged has been saved, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
        });
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
