package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.domain.tables.records.PayoutToolRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.Tables.PAYOUT_TOOL;

@Component
public class PayoutToolDaoImpl extends AbstractGenericDao implements PayoutToolDao {

    private final RowMapper<PayoutTool> payoutToolRowMapper;

    public PayoutToolDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.payoutToolRowMapper = new RecordRowMapper<>(PAYOUT_TOOL, PayoutTool.class);
    }

    @Override
    public void save(List<PayoutTool> payoutToolList) throws DaoException {
        //todo: Batch insert
        for (PayoutTool payoutTool : payoutToolList) {
            PayoutToolRecord record = getDslContext().newRecord(PAYOUT_TOOL, payoutTool);
            Query query = getDslContext().insertInto(PAYOUT_TOOL).set(record);
            executeOne(query);
        }
    }

    @Override
    public List<PayoutTool> getByCntrctId(Long cntrctId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYOUT_TOOL)
                .where(PAYOUT_TOOL.CNTRCT_ID.eq(cntrctId));
        return fetch(query, payoutToolRowMapper);
    }
}
