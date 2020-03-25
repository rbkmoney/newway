package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.ShopAccount;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.domain.tables.pojos.Shop;

public class ShopUtil {

    public static void fillShopAccount(com.rbkmoney.newway.domain.tables.pojos.Shop shop, ShopAccount shopAccount) {
        shop.setAccountCurrencyCode(shopAccount.getCurrency().getSymbolicCode());
        shop.setAccountGuarantee(shopAccount.getGuarantee());
        shop.setAccountSettlement(shopAccount.getSettlement());
        shop.setAccountPayout(shopAccount.getPayout());
    }

    public static void resetBaseFields(MachineEvent event, Integer changeId, long sequenceId, Shop shopSource) {
        shopSource.setId(null);
        shopSource.setRevision(null);
        shopSource.setWtime(null);
        shopSource.setSequenceId(sequenceId);
        shopSource.setChangeId(changeId);
        shopSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
    }
}
