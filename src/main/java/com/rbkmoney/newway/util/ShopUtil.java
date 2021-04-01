package com.rbkmoney.newway.util;

import com.rbkmoney.damsel.domain.ShopAccount;

public class ShopUtil {

    public static void fillShopAccount(com.rbkmoney.newway.domain.tables.pojos.Shop shop, ShopAccount shopAccount) {
        shop.setAccountCurrencyCode(shopAccount.getCurrency().getSymbolicCode());
        shop.setAccountGuarantee(shopAccount.getGuarantee());
        shop.setAccountSettlement(shopAccount.getSettlement());
        shop.setAccountPayout(shopAccount.getPayout());
    }

}
