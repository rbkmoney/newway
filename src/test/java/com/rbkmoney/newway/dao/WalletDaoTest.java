package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class WalletDaoTest extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WalletDao walletDao;

    @Test
    public void walletDaoTest() {
        jdbcTemplate.execute("truncate table nw.wallet cascade");
        Wallet wallet = random(Wallet.class);
        wallet.setCurrent(true);
        Long id = walletDao.save(wallet).get();
        wallet.setId(id);
        Wallet actual = walletDao.get(wallet.getWalletId());
        assertEquals(wallet, actual);
        walletDao.updateNotCurrent(actual.getId());
        Assert.assertNull(walletDao.get(wallet.getWalletId()));

        //check duplicate not error
        walletDao.save(wallet);
    }

}
