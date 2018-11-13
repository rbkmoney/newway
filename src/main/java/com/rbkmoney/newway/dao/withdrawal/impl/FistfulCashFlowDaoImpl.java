package com.rbkmoney.newway.dao.withdrawal.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.withdrawal.iface.FistfulCashFlowDao;
import com.rbkmoney.newway.domain.enums.FistfulCashFlowChangeType;
import com.rbkmoney.newway.domain.tables.pojos.FistfulCashFlow;
import com.rbkmoney.newway.domain.tables.records.FistfulCashFlowRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.tables.FistfulCashFlow.FISTFUL_CASH_FLOW;

@Component
public class FistfulCashFlowDaoImpl extends AbstractGenericDao implements FistfulCashFlowDao {

    private final RowMapper<FistfulCashFlow> cashFlowRowMapper;

    public FistfulCashFlowDaoImpl(DataSource dataSource) {
        super(dataSource);
        cashFlowRowMapper = new RecordRowMapper<>(FISTFUL_CASH_FLOW, FistfulCashFlow.class);
    }

    @Override
    public void save(List<FistfulCashFlow> cashFlowList) throws DaoException {
        //todo: Batch insert
        for (FistfulCashFlow paymentCashFlow : cashFlowList) {
            FistfulCashFlowRecord record = getDslContext().newRecord(FISTFUL_CASH_FLOW, paymentCashFlow);
            Query query = getDslContext().insertInto(FISTFUL_CASH_FLOW).set(record);
            executeOne(query);
        }
    }

    @Override
    public List<FistfulCashFlow> getByObjId(Long objId, FistfulCashFlowChangeType cashFlowChangeType) throws DaoException {
        Query query = getDslContext().selectFrom(FISTFUL_CASH_FLOW)
                .where(FISTFUL_CASH_FLOW.OBJ_ID.eq(objId))
                .and(FISTFUL_CASH_FLOW.OBJ_TYPE.eq(cashFlowChangeType));
        return fetch(query, cashFlowRowMapper);
    }

}
