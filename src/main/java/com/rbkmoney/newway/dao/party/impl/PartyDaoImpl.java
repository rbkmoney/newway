package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.domain.tables.records.PartyRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.*;

@Component
public class PartyDaoImpl extends AbstractGenericDao implements PartyDao {

    private final RowMapper<Party> partyRowMapper;

    public PartyDaoImpl(DataSource dataSource) {
        super(dataSource);
        partyRowMapper = new RecordRowMapper<>(PARTY, Party.class);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("event_id"))).from(
                getDslContext().select(PARTY.EVENT_ID.max().as("event_id")).from(PARTY)
                        .unionAll(getDslContext().select(CONTRACT.EVENT_ID.max().as("event_id")).from(CONTRACT))
                        .unionAll(getDslContext().select(CONTRACTOR.EVENT_ID.max().as("event_id")).from(CONTRACTOR))
                        .unionAll(getDslContext().select(SHOP.EVENT_ID.max().as("event_id")).from(SHOP))
        );
        return fetchOne(query, Long.class);
    }

    @Override
    public Long save(Party party) throws DaoException {
        PartyRecord record = getDslContext().newRecord(PARTY, party);
        Query query = getDslContext().insertInto(PARTY).set(record).returning(PARTY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Party get(String partyId) throws DaoException {
        Query query = getDslContext().selectFrom(PARTY)
                .where(PARTY.PARTY_ID.eq(partyId).and(PARTY.CURRENT));

        return fetchOne(query, partyRowMapper);
    }

    @Override
    public void update(String partyId) throws DaoException {
        Query query = getDslContext().update(PARTY).set(PARTY.CURRENT, false)
                .where(PARTY.PARTY_ID.eq(partyId).and(PARTY.CURRENT));
        executeOne(query);
    }
}
