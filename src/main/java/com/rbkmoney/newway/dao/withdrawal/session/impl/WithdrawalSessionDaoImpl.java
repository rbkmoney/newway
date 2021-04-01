package com.rbkmoney.newway.dao.withdrawal.session.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.withdrawal.session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.tables.pojos.WithdrawalSession;
import com.rbkmoney.newway.domain.tables.records.WithdrawalSessionRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import java.util.Optional;

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
    public Optional<Long> save(WithdrawalSession withdrawalSession) throws DaoException {
        WithdrawalSessionRecord record = getDslContext().newRecord(WITHDRAWAL_SESSION, withdrawalSession);
        Query query = getDslContext()
                .insertInto(WITHDRAWAL_SESSION)
                .set(record)
                .onConflict(WITHDRAWAL_SESSION.WITHDRAWAL_SESSION_ID, WITHDRAWAL_SESSION.SEQUENCE_ID)
                .doNothing()
                .returning(WITHDRAWAL_SESSION.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @NotNull
    @Override
    public WithdrawalSession get(String sessionId) throws DaoException {
        Query query = getDslContext().selectFrom(WITHDRAWAL_SESSION)
                .where(WITHDRAWAL_SESSION.WITHDRAWAL_SESSION_ID.eq(sessionId)
                        .and(WITHDRAWAL_SESSION.CURRENT));
        return Optional.ofNullable(fetchOne(query, withdrawalSessionRowMapper))
                .orElseThrow(() -> new NotFoundException(
                        String.format("WithdrawalSession not found, sessionId='%s'", sessionId)));
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(WITHDRAWAL_SESSION).set(WITHDRAWAL_SESSION.CURRENT, false)
                .where(WITHDRAWAL_SESSION.ID.eq(id)
                        .and(WITHDRAWAL_SESSION.CURRENT));
        execute(query);
    }
}
