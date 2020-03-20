package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.domain.Blocking;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractPartyManagementHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopBlockingHandler extends AbstractPartyManagementHandler {

    private final ShopDao shopDao;
    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "shop_blocking",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        Blocking blocking = change.getShopBlocking().getBlocking();
        String shopId = change.getShopBlocking().getShopId();
        String partyId = event.getSourceId();
        log.info("Start shop blocking handling, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
        Shop shopSource = shopDao.get(partyId, shopId);
        if (shopSource == null) {
            throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
        }
        shopSource.setId(null);
        shopSource.setRevision(null);
        shopSource.setWtime(null);
        shopSource.setSequenceId(sequenceId);
        shopSource.setChangeId(changeId);
        shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        shopSource.setBlocking(TBaseUtil.unionFieldToEnum(blocking, com.rbkmoney.newway.domain.enums.Blocking.class));
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
        shopDao.save(shopSource);
        shopDao.switchCurrent(partyId, shopId);
        log.info("Shop blocking has been saved, sequenceId={}, partyId={}, shopId={}, changeId={}",
                sequenceId, partyId, shopId, changeId);
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
