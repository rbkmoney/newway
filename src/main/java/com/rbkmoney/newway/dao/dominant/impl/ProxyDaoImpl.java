package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.Proxy;
import com.rbkmoney.newway.domain.tables.records.ProxyRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.newway.domain.Tables.PROXY;

@Component
public class ProxyDaoImpl extends AbstractGenericDao implements DomainObjectDao<Proxy, Integer> {

    public ProxyDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(Proxy proxy) throws DaoException {
        ProxyRecord proxyRecord = getDslContext().newRecord(PROXY, proxy);
        Query query = getDslContext().insertInto(PROXY).set(proxyRecord).returning(PROXY.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer proxyId) throws DaoException {
        Query query = getDslContext().update(PROXY).set(PROXY.CURRENT, false)
                .where(PROXY.PROXY_REF_ID.eq(proxyId).and(PROXY.CURRENT));
        executeOne(query);
    }
}
