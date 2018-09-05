package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.newway.dao.common.iface.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.DaoException;

import java.util.List;

public interface ShopDao extends GenericDao {
    Long save(Shop shop) throws DaoException;
    Shop get(String partyId, String shopId) throws DaoException;
    void updateNotCurrent(String partyId, String shopId) throws DaoException;
    List<Shop> getByPartyId(String partyId);
}
