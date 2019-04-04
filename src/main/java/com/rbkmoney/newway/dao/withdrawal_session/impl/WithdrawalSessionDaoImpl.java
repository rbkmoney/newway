package com.rbkmoney.newway.dao.withdrawal_session.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.domain.tables.records.WithdrawalSessionRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.WithdrawalSession.WITHDRAWAL_SESSION;

@Component
public class WithdrawalSessionDaoImpl extends AbstractGenericDao implements WithdrawalSessionDao {

    private final RowMapper<WithdrawalSession> withdrawalSessionRowMapper;

    @Autowired
    public WithdrawalSessionDaoImpl(@Qualifier("dataSource") DataSource dataSource) {
        super(dataSource);
        withdrawalSessionRowMapper = new RecordRowMapper<>(WITHDRAWAL_SESSION, WithdrawalSession.class);
    }


    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(WITHDRAWAL_SESSION.EVENT_ID)).from(WITHDRAWAL_SESSION);
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(WithdrawalSession withdrawalSession) throws DaoException {
        WithdrawalSessionRecord record = getDslContext().newRecord(WITHDRAWAL_SESSION, withdrawalSession);
        Query query = getDslContext().insertInto(WITHDRAWAL_SESSION).set(record).returning(WITHDRAWAL_SESSION.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public WithdrawalSession get(String sessionId) throws DaoException {
        Query query = getDslContext().selectFrom(WITHDRAWAL_SESSION)
                .where(WITHDRAWAL_SESSION.WITHDRAWAL_SESSION_ID.eq(sessionId)
                        .and(WITHDRAWAL_SESSION.CURRENT));
        return fetchOne(query, withdrawalSessionRowMapper);
    }

    @Override
    public void updateNotCurrent(String sessionId) throws DaoException {
        Query query = getDslContext().update(WITHDRAWAL_SESSION).set(WITHDRAWAL_SESSION.CURRENT, false)
                .where(WITHDRAWAL_SESSION.WITHDRAWAL_SESSION_ID.eq(sessionId)
                                .and(WITHDRAWAL_SESSION.CURRENT));
        execute(query);
    }
}
