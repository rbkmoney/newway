package com.rbkmoney.newway.dao.identity.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.domain.tables.records.IdentityRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.Optional;

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
    public Optional<Long> save(Identity identity) throws DaoException {
        IdentityRecord record = getDslContext().newRecord(IDENTITY, identity);
        Query query = getDslContext()
                .insertInto(IDENTITY)
                .set(record)
                .onConflict(IDENTITY.IDENTITY_ID, IDENTITY.SEQUENCE_ID)
                .doNothing()
                .returning(IDENTITY.ID);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Identity get(String identityId) throws DaoException {
        Query query = getDslContext().selectFrom(IDENTITY)
                .where(IDENTITY.IDENTITY_ID.eq(identityId)
                        .and(IDENTITY.CURRENT));

        return fetchOne(query, identityRowMapper);
    }

    @Override
    public void updateNotCurrent(Long identityId) throws DaoException {
        Query query = getDslContext().update(IDENTITY).set(IDENTITY.CURRENT, false)
                .where(
                        IDENTITY.ID.eq(identityId)
                                .and(IDENTITY.CURRENT)
                );
        execute(query);
    }
}
