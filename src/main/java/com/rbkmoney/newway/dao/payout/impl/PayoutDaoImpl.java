package com.rbkmoney.newway.dao.payout.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.domain.tables.pojos.Payout;
import com.rbkmoney.newway.domain.tables.records.PayoutRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.newway.domain.Tables.PAYOUT;

@Component
public class PayoutDaoImpl extends AbstractGenericDao implements PayoutDao {

    private final RowMapper<Payout> payoutRowMapper;

    @Autowired
    public PayoutDaoImpl(DataSource dataSource) {
        super(dataSource);
        payoutRowMapper = new RecordRowMapper<>(PAYOUT, Payout.class);
    }

    @Override
    public Optional<Long> save(Payout payout) throws DaoException {
        PayoutRecord payoutRecord = getDslContext().newRecord(PAYOUT, payout);
        Query query = getDslContext()
                .insertInto(PAYOUT)
                .set(payoutRecord)
                .onConflict(PAYOUT.EVENT_ID, PAYOUT.PAYOUT_ID, PAYOUT.CHANGE_ID)
                .doNothing()
                .returning(PAYOUT.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Payout get(String payoutId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYOUT)
                .where(PAYOUT.PAYOUT_ID.eq(payoutId).and(PAYOUT.CURRENT));
        return fetchOne(query, payoutRowMapper);
    }

    @Override
    public void updateNotCurrent(Long payoutId) throws DaoException {
        Query query = getDslContext().update(PAYOUT).set(PAYOUT.CURRENT, false)
                .where(PAYOUT.ID.eq(payoutId)
                        .and(PAYOUT.CURRENT));
        executeOne(query);
    }
}
