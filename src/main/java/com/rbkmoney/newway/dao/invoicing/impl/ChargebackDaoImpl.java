package com.rbkmoney.newway.dao.invoicing.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.invoicing.iface.ChargebackDao;
import com.rbkmoney.newway.domain.tables.pojos.Chargeback;
import com.rbkmoney.newway.domain.tables.records.ChargebackRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import java.util.Optional;

import static com.rbkmoney.newway.domain.tables.Chargeback.CHARGEBACK;

@Component
public class ChargebackDaoImpl extends AbstractGenericDao implements ChargebackDao {

    private final RowMapper<Chargeback> chargebackRowMapper;

    @Autowired
    public ChargebackDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.chargebackRowMapper = new RecordRowMapper<>(CHARGEBACK, Chargeback.class);
    }

    @Override
    public Optional<Long> save(Chargeback chargeback) throws DaoException {
        ChargebackRecord chargebackRecord = getDslContext().newRecord(CHARGEBACK, chargeback);
        Query query = getDslContext().insertInto(CHARGEBACK)
                .set(chargebackRecord)
                .onConflict(CHARGEBACK.INVOICE_ID, CHARGEBACK.SEQUENCE_ID, CHARGEBACK.CHANGE_ID)
                .doNothing()
                .returning(CHARGEBACK.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @NotNull
    @Override
    public Chargeback get(String invoiceId, String paymentId, String chargebackId) throws DaoException {
        Query query = getDslContext().selectFrom(CHARGEBACK)
                .where(CHARGEBACK.INVOICE_ID.eq(invoiceId)
                        .and(CHARGEBACK.PAYMENT_ID.eq(paymentId))
                        .and(CHARGEBACK.CHARGEBACK_ID.eq(chargebackId))
                        .and(CHARGEBACK.CURRENT));

        return Optional.ofNullable(fetchOne(query, chargebackRowMapper))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Chargeback not found, invoiceId='%s', paymentId='%s', chargebackId='%s'",
                                invoiceId, paymentId, chargebackId)));
    }

    @Override
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext().update(CHARGEBACK).set(CHARGEBACK.CURRENT, false).where(CHARGEBACK.ID.eq(id));
        executeOne(query);
    }
}
