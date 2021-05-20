package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.TradeBloc;
import com.rbkmoney.newway.domain.tables.records.TradeBlocRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.TRADE_BLOC;

@Component
public class TradeBlocDaoImpl extends AbstractGenericDao implements DomainObjectDao<TradeBloc, String> {

    public TradeBlocDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(TradeBloc tradeBloc) throws DaoException {
        TradeBlocRecord tradeBlocRecord = getDslContext().newRecord(TRADE_BLOC, tradeBloc);
        Query query = getDslContext().insertInto(TRADE_BLOC).set(tradeBlocRecord).returning(TRADE_BLOC.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(String tradeBlocId) throws DaoException {
        Query query = getDslContext().update(TRADE_BLOC).set(TRADE_BLOC.CURRENT, false)
                .where(TRADE_BLOC.TRADE_BLOC_REF_ID.eq(tradeBlocId).and(TRADE_BLOC.CURRENT));
        executeOne(query);
    }
}
