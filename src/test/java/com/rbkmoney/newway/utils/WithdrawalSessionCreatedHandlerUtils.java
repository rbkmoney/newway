package com.rbkmoney.newway.utils;

import com.rbkmoney.fistful.base.CryptoCurrency;
import com.rbkmoney.fistful.base.CryptoData;
import com.rbkmoney.fistful.base.CryptoDataBitcoin;
import com.rbkmoney.fistful.base.DigitalData;
import com.rbkmoney.fistful.base.DigitalDataWebmoney;
import com.rbkmoney.fistful.base.Resource;
import com.rbkmoney.fistful.base.ResourceBankCard;
import com.rbkmoney.fistful.base.ResourceCryptoWallet;
import com.rbkmoney.fistful.base.ResourceDigitalWallet;
import com.rbkmoney.fistful.withdrawal_session.Change;
import com.rbkmoney.fistful.withdrawal_session.Route;
import com.rbkmoney.fistful.withdrawal_session.TimestampedChange;
import com.rbkmoney.fistful.withdrawal_session.Withdrawal;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;

public class WithdrawalSessionCreatedHandlerUtils {

    public static final String WITHDRAWAL_ID = "withdrawal_id";
    public static final String SESSION_ID = "session_id";

    public static final int PROVIDER_ID = 1;
    public static final int TERMINAL_ID = 1;

    public static final String DIGITAL_WALLET_ID = "digital_wallet_id";
    public static final String CRYPTO_WALLET_ID = "crypto_wallet_id";

    public static final String CARD_BIN = "bin";
    public static final String CARD_MASKED_PAN = "1232132";
    public static final String CARD_TOKEN_PROVIDER = "cardToken";

    public static MachineEvent createCreatedMachineEvent(String id,
                                                         com.rbkmoney.fistful.withdrawal_session.Session session) {
        return new MachineEvent()
                .setEventId(2L)
                .setSourceId(id)
                .setSourceNs("2")
                .setCreatedAt("2021-05-31T06:12:27Z")
                .setData(Value.bin(new ThriftSerializer<>().serialize("", createCreated(session))));
    }

    public static TimestampedChange createCreated(com.rbkmoney.fistful.withdrawal_session.Session session) {
        return new TimestampedChange()
                .setOccuredAt("2021-05-31T06:12:27Z")
                .setChange(Change.created(session));
    }

    public static com.rbkmoney.fistful.base.DigitalWallet createFistfulDigitalWallet() {
        com.rbkmoney.fistful.base.DigitalWallet digitalWallet = new com.rbkmoney.fistful.base.DigitalWallet();
        digitalWallet.setId(DIGITAL_WALLET_ID);
        digitalWallet.setData(DigitalData.webmoney(new DigitalDataWebmoney()));
        return digitalWallet;
    }

    public static com.rbkmoney.fistful.base.CryptoWallet createFistfulCryptoWallet() {
        com.rbkmoney.fistful.base.CryptoWallet cryptoWallet = new com.rbkmoney.fistful.base.CryptoWallet();
        cryptoWallet.setId(CRYPTO_WALLET_ID);
        cryptoWallet.setData(CryptoData.bitcoin(new CryptoDataBitcoin()));
        cryptoWallet.setCurrency(CryptoCurrency.bitcoin);
        return cryptoWallet;
    }

    public static com.rbkmoney.fistful.base.BankCard createFistfulBankCard() {
        com.rbkmoney.fistful.base.BankCard bankCard = new com.rbkmoney.fistful.base.BankCard();
        bankCard.setToken(CARD_TOKEN_PROVIDER);
        bankCard.setBin(CARD_BIN);
        bankCard.setMaskedPan(CARD_MASKED_PAN);
        return bankCard;
    }

    public static ResourceDigitalWallet createDestinationResourceDigitalWallet() {
        ResourceDigitalWallet resourceDigitalWallet = new ResourceDigitalWallet();
        resourceDigitalWallet.setDigitalWallet(createFistfulDigitalWallet());
        return resourceDigitalWallet;
    }

    public static ResourceCryptoWallet createDestinationResourceCryptoWallet() {
        ResourceCryptoWallet resourceCryptoWallet = new ResourceCryptoWallet();
        resourceCryptoWallet.setCryptoWallet(createFistfulCryptoWallet());
        return resourceCryptoWallet;
    }

    public static ResourceBankCard createDestinationResourceBankCard() {
        ResourceBankCard resourceBankCard = new ResourceBankCard();
        resourceBankCard.setBankCard(createFistfulBankCard());
        return resourceBankCard;
    }

    public static com.rbkmoney.fistful.withdrawal_session.Session createSession(Resource resource) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(WITHDRAWAL_ID);
        withdrawal.setDestinationResource(resource);

        com.rbkmoney.fistful.base.Cash cash = new com.rbkmoney.fistful.base.Cash();
        cash.setAmount(11L);
        cash.setCurrency(new com.rbkmoney.fistful.base.CurrencyRef("RUB"));
        withdrawal.setCash(cash);
        com.rbkmoney.fistful.withdrawal_session.Session session = new com.rbkmoney.fistful.withdrawal_session.Session();
        session.setId(SESSION_ID);
        session.setWithdrawal(withdrawal);
        session.setRoute(createRoute());
        return session;
    }

    public static Route createRoute() {
        Route route = new Route();
        route.setProviderId(PROVIDER_ID);
        route.setTerminalId(TERMINAL_ID);
        return route;
    }

}
