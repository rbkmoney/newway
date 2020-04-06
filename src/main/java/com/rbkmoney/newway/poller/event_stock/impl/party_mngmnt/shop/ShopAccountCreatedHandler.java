package com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.shop;

import com.rbkmoney.damsel.domain.ShopAccount;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.ShopEffectUnit;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.AbstractClaimChangedHandler;
import com.rbkmoney.newway.util.ShopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class ShopAccountCreatedHandler extends AbstractClaimChangedHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ShopDao shopDao;

    public ShopAccountCreatedHandler(ShopDao shopDao) {
        this.shopDao = shopDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, Event event, Integer changeId) {
        long eventId = event.getId();
        List<ClaimEffect> claimEffects = getClaimStatus(change).getAccepted().getEffects().stream()
                .filter(e -> e.isSetShopEffect() && e.getShopEffect().getEffect().isSetAccountCreated())
                .collect(Collectors.toList());
        for (int i = 0; i < claimEffects.size(); i++) {
            ClaimEffect claimEffect = claimEffects.get(i);
            ShopEffectUnit shopEffect = claimEffect.getShopEffect();
            ShopAccount accountCreated = shopEffect.getEffect().getAccountCreated();
            String shopId = shopEffect.getShopId();
            String partyId = event.getSource().getPartyId();
            log.info("Start shop accountCreated handling, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
            Shop shopSource = shopDao.get(partyId, shopId);
            if (shopSource == null) {
                throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
            }
            shopSource.setId(null);
            shopSource.setRevision(null);
            shopSource.setWtime(null);
            shopSource.setEventId(eventId);
            shopSource.setChangeId(changeId);
            shopSource.setClaimEffectId(i);
            shopSource.setSequenceId(event.getSequence());
            shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            ShopUtil.fillShopAccount(shopSource, accountCreated);

            shopDao.updateNotCurrent(partyId, shopId);
            shopDao.save(shopSource);
            log.info("Shop accountCreated has been saved, eventId={}, partyId={}, shopId={}", eventId, partyId, shopId);
        }
    }
}
