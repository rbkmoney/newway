package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.ContractorDao;
import com.rbkmoney.newway.domain.tables.pojos.Contractor;
import com.rbkmoney.newway.domain.tables.records.ContractorRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.Tables.CONTRACTOR;
import static com.rbkmoney.newway.domain.Tables.PARTY;

@Component
public class ContractorDaoImpl extends AbstractGenericDao implements ContractorDao {

    private final RowMapper<Contractor> contractorRowMapper;

    public ContractorDaoImpl(DataSource dataSource) {
        super(dataSource);
        contractorRowMapper = new RecordRowMapper<>(CONTRACTOR, Contractor.class);
    }

    @Override
    public Long save(Contractor contractor) throws DaoException {
        ContractorRecord record = getDslContext().newRecord(CONTRACTOR, contractor);
        Query query = getDslContext().insertInto(CONTRACTOR).set(record)
                .onConflict(CONTRACTOR.PARTY_ID, CONTRACTOR.SEQUENCE_ID, CONTRACTOR.CHANGE_ID)
                .doNothing()
                .returning(CONTRACTOR.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Contractor get(String partyId, String contractorId) throws DaoException {
        Query query = getDslContext().selectFrom(CONTRACTOR)
                .where(CONTRACTOR.PARTY_ID.eq(partyId).and(CONTRACTOR.CONTRACTOR_ID.eq(contractorId)).and(CONTRACTOR.CURRENT));

        return fetchOne(query, contractorRowMapper);
    }

    @Override
    public void switchCurrent(String partyId, String contractorId) throws DaoException {
        this.getNamedParameterJdbcTemplate().update("update nw.contractor set current = false where party_id =:party_id and contractor_id =:contractor_id and current;" +
                        "update nw.contractor set current = true where id = (select max(id) from nw.contractor where party_id =:party_id and contractor_id =:contractor_id);",
                new MapSqlParameterSource("party_id", partyId)
                        .addValue("contractor_id", contractorId));
    }

    @Override
    public List<Contractor> getByPartyId(String partyId) {
        Query query = getDslContext().selectFrom(CONTRACTOR)
                .where(CONTRACTOR.PARTY_ID.eq(partyId).and(CONTRACTOR.CURRENT));
        return fetch(query, contractorRowMapper);
    }
}
