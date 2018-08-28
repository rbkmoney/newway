package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.domain.Suspension;
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
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ShopSuspensionHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ShopDao shopDao;
    private final Filter filter;

    public ShopSuspensionHandler(ShopDao shopDao) {
        this.shopDao = shopDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "shop_suspension",
                new IsNullCondition().not()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        Suspension suspension = change.getShopSuspension().getSuspension();
        String shopId = change.getShopSuspension().getShopId();
        String partyId = event.getSource().getPartyId();
        log.info("Start shop suspension handling, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
        Shop shopSource = shopDao.get(shopId);
        if (shopSource == null) {
            throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
        }
        shopSource.setId(null);
        shopSource.setWtime(null);
        shopSource.setEventId(eventId);
        shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        com.rbkmoney.newway.domain.enums.Suspension suspensionType = TypeUtil.toEnumField(suspension.getSetField().getFieldName(), com.rbkmoney.newway.domain.enums.Suspension.class);
        if (suspensionType == null) {
            throw new IllegalArgumentException("Illegal shop suspension: " + suspensionType);
        }
        shopSource.setSuspension(suspensionType);
        if (suspension.isSetActive()) {
            shopSource.setSuspensionActiveSince(TypeUtil.stringToLocalDateTime(suspension.getActive().getSince()));
            shopSource.setSuspensionSuspendedSince(null);
        } else if (suspension.isSetSuspended()) {
            shopSource.setSuspensionActiveSince(null);
            shopSource.setSuspensionSuspendedSince(TypeUtil.stringToLocalDateTime(suspension.getSuspended().getSince()));
        }
        shopDao.updateNotCurrent(shopId);
        shopDao.save(shopSource);
        log.info("Shop suspension has been saved, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
