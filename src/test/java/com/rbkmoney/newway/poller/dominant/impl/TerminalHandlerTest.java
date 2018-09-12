package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.dominant.impl.TerminalDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Terminal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

public class TerminalHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private TerminalDaoImpl terminalDao;

    @Test
    public void test() {
        Terminal category = random(Terminal.class);
        category.setCurrent(true);
        terminalDao.save(category);
        terminalDao.updateNotCurrent(category.getTerminalRefId());
    }
}
