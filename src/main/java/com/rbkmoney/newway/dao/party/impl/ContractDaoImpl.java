package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.records.ContractRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.Tables.CONTRACT;

@Component
public class ContractDaoImpl extends AbstractGenericDao implements ContractDao {

    private final RowMapper<Contract> contractRowMapper;

    public ContractDaoImpl(DataSource dataSource) {
        super(dataSource);
        contractRowMapper = new RecordRowMapper<>(CONTRACT, Contract.class);
    }

    @Override
    public Long save(Contract contract) throws DaoException {
        ContractRecord record = getDslContext().newRecord(CONTRACT, contract);
        Query query = getDslContext().insertInto(CONTRACT).set(record).returning(CONTRACT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Contract get(String partyId, String contractId) throws DaoException {
        Query query = getDslContext().selectFrom(CONTRACT)
                .where(CONTRACT.PARTY_ID.eq(partyId).and(CONTRACT.CONTRACT_ID.eq(contractId)).and(CONTRACT.CURRENT));

        return fetchOne(query, contractRowMapper);
    }

    @Override
    public void updateNotCurrent(String partyId, String contractId) throws DaoException {
        Query query = getDslContext().update(CONTRACT).set(CONTRACT.CURRENT, false)
                .where(CONTRACT.PARTY_ID.eq(partyId).and(CONTRACT.CONTRACT_ID.eq(contractId)).and(CONTRACT.CURRENT));
        executeOne(query);
    }

    @Override
    public List<Contract> getByPartyId(String partyId) {
        Query query = getDslContext().selectFrom(CONTRACT)
                .where(CONTRACT.PARTY_ID.eq(partyId).and(CONTRACT.CURRENT));
        return fetch(query, contractRowMapper);
    }
}
