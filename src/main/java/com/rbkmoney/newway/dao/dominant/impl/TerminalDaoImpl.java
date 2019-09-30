package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Terminal;
import com.rbkmoney.newway.domain.tables.records.TerminalRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.TERMINAL;

@Component
public class TerminalDaoImpl extends AbstractGenericDao implements DomainObjectDao<Terminal, Integer> {

    public TerminalDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Terminal terminal) throws DaoException {
        TerminalRecord terminalRecord = getDslContext().newRecord(TERMINAL, terminal);
        Query query = getDslContext().insertInto(TERMINAL).set(terminalRecord).returning(TERMINAL.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer terminalId) throws DaoException {
        Query query = getDslContext().update(TERMINAL).set(TERMINAL.CURRENT, false)
                .where(TERMINAL.TERMINAL_REF_ID.eq(terminalId).and(TERMINAL.CURRENT));
        executeOne(query);
    }
}
