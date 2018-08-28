package com.rbkmoney.newway.dao.payout.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.common.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.domain.tables.pojos.PayoutSummary;
import com.rbkmoney.newway.domain.tables.records.PayoutSummaryRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.Tables.PAYOUT_SUMMARY;

@Component
public class PayoutSummaryDaoImpl extends AbstractGenericDao implements PayoutSummaryDao {

    private final RowMapper<PayoutSummary> payoutSummaryRowMapper;

    @Autowired
    public PayoutSummaryDaoImpl(DataSource dataSource) {
        super(dataSource);
        payoutSummaryRowMapper = new RecordRowMapper<>(PAYOUT_SUMMARY, PayoutSummary.class);
    }

    @Override
    public void save(List<PayoutSummary> payoutSummaryList) throws DaoException {
        //todo: Batch insert
        for (PayoutSummary payoutSummary : payoutSummaryList) {
            PayoutSummaryRecord record = getDslContext().newRecord(PAYOUT_SUMMARY, payoutSummary);
            Query query = getDslContext().insertInto(PAYOUT_SUMMARY).set(record);
            executeOne(query);
        }
    }

    @Override
    public List<PayoutSummary> getByPytId(Long pytId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYOUT_SUMMARY)
                .where(PAYOUT_SUMMARY.PYT_ID.eq(pytId));
        return fetch(query, payoutSummaryRowMapper);
    }
}
