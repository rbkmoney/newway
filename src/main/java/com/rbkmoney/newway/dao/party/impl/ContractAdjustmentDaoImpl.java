package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.records.ContractAdjustmentRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.Tables.CONTRACT_ADJUSTMENT;

@Component
public class ContractAdjustmentDaoImpl extends AbstractGenericDao implements ContractAdjustmentDao {

    private final RowMapper<ContractAdjustment> contractAdjustmentRowMapper;

    public ContractAdjustmentDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.contractAdjustmentRowMapper = new RecordRowMapper<>(CONTRACT_ADJUSTMENT, ContractAdjustment.class);;
    }

    @Override
    public void save(List<ContractAdjustment> contractAdjustmentList) throws DaoException {
        //todo: Batch insert
        for (ContractAdjustment contractAdjustment : contractAdjustmentList) {
            ContractAdjustmentRecord record = getDslContext().newRecord(CONTRACT_ADJUSTMENT, contractAdjustment);
            Query query = getDslContext().insertInto(CONTRACT_ADJUSTMENT).set(record);
            executeOne(query);
        }
    }

    @Override
    public List<ContractAdjustment> getByCntrctId(Long cntrctId) throws DaoException {
        Query query = getDslContext().selectFrom(CONTRACT_ADJUSTMENT)
                .where(CONTRACT_ADJUSTMENT.CNTRCT_ID.eq(cntrctId))
                .orderBy(CONTRACT_ADJUSTMENT.ID.asc());
        return fetch(query, contractAdjustmentRowMapper);
    }
}
