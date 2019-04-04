package com.rbkmoney.newway.dao.identity.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.records.IdentityRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.tables.Challenge.CHALLENGE;
import static com.rbkmoney.newway.domain.tables.Identity.IDENTITY;

@Component
public class IdentityDaoImpl extends AbstractGenericDao implements IdentityDao {

    private final RowMapper<Identity> identityRowMapper;

    @Autowired
    public IdentityDaoImpl(DataSource dataSource) {
        super(dataSource);
        identityRowMapper = new RecordRowMapper<>(IDENTITY, Identity.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("event_id"))).from(
                getDslContext().select(IDENTITY.EVENT_ID.max().as("event_id")).from(IDENTITY)
                        .unionAll(getDslContext().select(CHALLENGE.EVENT_ID.max().as("event_id")).from(CHALLENGE))
        );
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Identity identity) throws DaoException {
        IdentityRecord record = getDslContext().newRecord(IDENTITY, identity);
        Query query = getDslContext().insertInto(IDENTITY).set(record).returning(IDENTITY.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Identity get(String identityId) throws DaoException {
        Query query = getDslContext().selectFrom(IDENTITY)
                .where(IDENTITY.IDENTITY_ID.eq(identityId)
                        .and(IDENTITY.CURRENT));

        return fetchOne(query, identityRowMapper);
    }

    @Override
    public void updateNotCurrent(String identityId) throws DaoException {
        Query query = getDslContext().update(IDENTITY).set(IDENTITY.CURRENT, false)
                .where(
                        IDENTITY.IDENTITY_ID.eq(identityId)
                                .and(IDENTITY.CURRENT)
                );
        execute(query);
    }
}
