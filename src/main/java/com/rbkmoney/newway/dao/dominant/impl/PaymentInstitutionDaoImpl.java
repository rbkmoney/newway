package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.PaymentInstitution;
import com.rbkmoney.newway.domain.tables.records.PaymentInstitutionRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.PAYMENT_INSTITUTION;

@Component
public class PaymentInstitutionDaoImpl extends AbstractGenericDao implements DomainObjectDao<PaymentInstitution, Integer> {

    public PaymentInstitutionDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(PaymentInstitution paymentInstitution) throws DaoException {
        PaymentInstitutionRecord paymentInstitutionRecord = getDslContext().newRecord(PAYMENT_INSTITUTION, paymentInstitution);
        Query query = getDslContext().insertInto(PAYMENT_INSTITUTION).set(paymentInstitutionRecord).returning(PAYMENT_INSTITUTION.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer paymentInstitutionId) throws DaoException {
        Query query = getDslContext().update(PAYMENT_INSTITUTION).set(PAYMENT_INSTITUTION.CURRENT, false)
                .where(PAYMENT_INSTITUTION.PAYMENT_INSTITUTION_REF_ID.eq(paymentInstitutionId).and(PAYMENT_INSTITUTION.CURRENT));
        executeOne(query);
    }
}
