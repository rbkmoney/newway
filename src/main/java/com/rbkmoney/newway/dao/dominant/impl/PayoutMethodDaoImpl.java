package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.PayoutMethod;
import com.rbkmoney.newway.domain.tables.records.PayoutMethodRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.PAYOUT_METHOD;

@Component
public class PayoutMethodDaoImpl extends AbstractGenericDao implements DomainObjectDao<PayoutMethod, String> {

    public PayoutMethodDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(PayoutMethod payoutMethod) throws DaoException {
        PayoutMethodRecord payoutMethodRecord = getDslContext().newRecord(PAYOUT_METHOD, payoutMethod);
        Query query = getDslContext().insertInto(PAYOUT_METHOD).set(payoutMethodRecord).returning(PAYOUT_METHOD.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(String payoutMethodId) throws DaoException {
        Query query = getDslContext().update(PAYOUT_METHOD).set(PAYOUT_METHOD.CURRENT, false)
                .where(PAYOUT_METHOD.PAYOUT_METHOD_REF_ID.eq(payoutMethodId).and(PAYOUT_METHOD.CURRENT));
        executeOne(query);
    }
}
