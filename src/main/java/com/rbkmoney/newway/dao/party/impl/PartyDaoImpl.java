package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.domain.tables.records.PartyRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.PARTY;

@Component
public class PartyDaoImpl extends AbstractGenericDao implements PartyDao {

    private final RowMapper<Party> partyRowMapper;

    public PartyDaoImpl(DataSource dataSource) {
        super(dataSource);
        partyRowMapper = new RecordRowMapper<>(PARTY, Party.class);
    }

    @Override
    public Long save(Party party) throws DaoException {
        PartyRecord record = getDslContext().newRecord(PARTY, party);
        Query query = getDslContext().insertInto(PARTY).set(record)
                .onConflict(PARTY.PARTY_ID, PARTY.SEQUENCE_ID, PARTY.CHANGE_ID)
                .doNothing()
                .returning(PARTY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Party get(String partyId) throws DaoException {
        Query query = getDslContext().selectFrom(PARTY)
                .where(PARTY.PARTY_ID.eq(partyId).and(PARTY.CURRENT));

        return fetchOne(query, partyRowMapper);
    }

    @Override
    public void switchCurrent(String partyId) throws DaoException {
        this.getNamedParameterJdbcTemplate().update("update nw.party set current = false where party_id =:party_id and current;" +
                        "update nw.party set current = true where id = (select max(id) from nw.party where party_id =:party_id);",
                new MapSqlParameterSource("party_id", partyId));
    }
}
