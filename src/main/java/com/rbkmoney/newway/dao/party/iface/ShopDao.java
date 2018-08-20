package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.DaoException;

public interface ShopDao extends GenericDao {
    Long save(Shop shop) throws DaoException;
    Shop get(String shopId) throws DaoException;
    void update(String shopId) throws DaoException;
}
