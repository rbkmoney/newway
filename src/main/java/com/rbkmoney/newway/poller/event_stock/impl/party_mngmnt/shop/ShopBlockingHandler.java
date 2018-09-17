package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.domain.Blocking;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ShopBlockingHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ShopDao shopDao;
    private final Filter filter;

    public ShopBlockingHandler(ShopDao shopDao) {
        this.shopDao = shopDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "shop_blocking",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        Blocking blocking = change.getShopBlocking().getBlocking();
        String shopId = change.getShopBlocking().getShopId();
        String partyId = event.getSource().getPartyId();
        log.info("Start shop blocking handling, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
        Shop shopSource = shopDao.get(partyId, shopId);
        if (shopSource == null) {
            throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
        }
        shopSource.setId(null);
        shopSource.setWtime(null);
        shopSource.setEventId(eventId);
        shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        com.rbkmoney.newway.domain.enums.Blocking blockingType = TypeUtil.toEnumField(blocking.getSetField().getFieldName(), com.rbkmoney.newway.domain.enums.Blocking.class);
        if (blockingType == null) {
            throw new IllegalArgumentException("Illegal shop blocking: " + blocking);
        }
        shopSource.setBlocking(blockingType);
        if (blocking.isSetUnblocked()) {
            shopSource.setBlockingUnblockedReason(blocking.getUnblocked().getReason());
            shopSource.setBlockingUnblockedSince(TypeUtil.stringToLocalDateTime(blocking.getUnblocked().getSince()));
            shopSource.setBlockingBlockedReason(null);
            shopSource.setBlockingBlockedSince(null);
        } else if (blocking.isSetBlocked()) {
            shopSource.setBlockingUnblockedReason(null);
            shopSource.setBlockingUnblockedSince(null);
            shopSource.setBlockingBlockedReason(blocking.getBlocked().getReason());
            shopSource.setBlockingBlockedSince(TypeUtil.stringToLocalDateTime(blocking.getBlocked().getSince()));
        }
        shopDao.updateNotCurrent(partyId, shopId);
        shopDao.save(shopSource);
        log.info("Shop blocking has been saved, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
