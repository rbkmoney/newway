package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.records.ContractRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rbkmoney.newway.domain.Tables.CONTRACT;

@Component
public class ContractDaoImpl extends AbstractGenericDao implements ContractDao {

    private final RowMapper<Contract> contractRowMapper;

    public ContractDaoImpl(DataSource dataSource) {
        super(dataSource);
        contractRowMapper = new RecordRowMapper<>(CONTRACT, Contract.class);
    }

    @Override
    public Optional<Long> save(Contract contract) throws DaoException {
        ContractRecord record = getDslContext().newRecord(CONTRACT, contract);
        Query query = getDslContext().insertInto(CONTRACT).set(record)
                .onConflict(CONTRACT.PARTY_ID, CONTRACT.CONTRACT_ID, CONTRACT.SEQUENCE_ID, CONTRACT.CHANGE_ID,
                        CONTRACT.CLAIM_EFFECT_ID, CONTRACT.REVISION)
                .doNothing()
                .returning(CONTRACT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public void saveBatch(List<Contract> contracts) throws DaoException {
        List<Query> queries = contracts.stream()
                .map(contractor -> getDslContext().newRecord(CONTRACT, contractor))
                .map(contractorRecord -> getDslContext().insertInto(CONTRACT)
                        .set(contractorRecord)
                        .onConflict(CONTRACT.PARTY_ID, CONTRACT.CONTRACT_ID, CONTRACT.SEQUENCE_ID, CONTRACT.CHANGE_ID,
                                CONTRACT.CLAIM_EFFECT_ID, CONTRACT.REVISION)
                        .doNothing()
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public Contract get(String partyId, String contractId) throws DaoException {
        Query query = getDslContext().selectFrom(CONTRACT)
                .where(CONTRACT.PARTY_ID.eq(partyId).and(CONTRACT.CONTRACT_ID.eq(contractId)).and(CONTRACT.CURRENT));

        Contract contract = fetchOne(query, contractRowMapper);
        if (contract == null) {
            throw new NotFoundException(String.format("Contract not found, contractId='%s'", contractId));
        }
        return contract;
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(CONTRACT).set(CONTRACT.CURRENT, false)
                .where(CONTRACT.ID.eq(id));
        executeOne(query);
    }

    @Override
    public void updateNotCurrent(List<Long> ids) throws DaoException {
        Query query = getDslContext().update(CONTRACT).set(CONTRACT.CURRENT, false).where(CONTRACT.ID.in(ids));
        execute(query);
    }

    @Override
    public void switchCurrent(List<String> ids, String partyId) throws DaoException {
        if (!CollectionUtils.isEmpty(ids)) {
            this.getNamedParameterJdbcTemplate()
                    .update("update nw.contract set current = false where contract_id in(:contract_ids) and party_id=:party_id and current;" +
                                    "update nw.contract set current = true where id in(" +
                                    "    SELECT max(id)" +
                                    "    FROM nw.contract" +
                                    "    where contract_id in (:contract_ids)" +
                                    "    and party_id=:party_id" +
                                    "    group by contract_id, party_id);",
                            new MapSqlParameterSource(Map.of("contract_ids", ids, "party_id", partyId)));
        }
    }

    @Override
    public List<Contract> getByPartyId(String partyId) {
        Query query = getDslContext().selectFrom(CONTRACT)
                .where(CONTRACT.PARTY_ID.eq(partyId).and(CONTRACT.CURRENT));
        return fetch(query, contractRowMapper);
    }
}
