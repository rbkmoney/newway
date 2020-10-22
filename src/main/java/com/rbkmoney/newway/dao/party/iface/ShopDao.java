package com.rbkmoney.newway.dao.party.iface;

import com.rbkmoney.dao.GenericDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.exception.DaoException;

import java.util.Optional;

public interface ShopDao extends GenericDao {

    Optional<Long> save(Shop shop) throws DaoException;

    Shop get(String partyId, String shopId) throws DaoException;

    void updateNotCurrent(Long id) throws DaoException;

    void saveWithUpdateCurrent(Shop shopSource, Long oldEventId, String eventName);
}
