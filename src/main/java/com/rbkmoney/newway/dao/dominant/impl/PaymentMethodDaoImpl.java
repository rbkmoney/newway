package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.newway.dao.common.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.PaymentMethod;
import com.rbkmoney.newway.domain.tables.records.PaymentMethodRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.PAYMENT_METHOD;

@Component
public class PaymentMethodDaoImpl extends AbstractGenericDao implements DomainObjectDao<PaymentMethod, String> {

    public PaymentMethodDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(PaymentMethod paymentMethod) throws DaoException {
        PaymentMethodRecord paymentMethodRecord = getDslContext().newRecord(PAYMENT_METHOD, paymentMethod);
        Query query = getDslContext().insertInto(PAYMENT_METHOD).set(paymentMethodRecord).returning(PAYMENT_METHOD.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOneWithReturn(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(String paymentMethodId) throws DaoException {
        Query query = getDslContext().update(PAYMENT_METHOD).set(PAYMENT_METHOD.CURRENT, false)
                .where(PAYMENT_METHOD.PAYMENT_METHOD_REF_ID.eq(paymentMethodId).and(PAYMENT_METHOD.CURRENT));
        executeOne(query);
    }
}
