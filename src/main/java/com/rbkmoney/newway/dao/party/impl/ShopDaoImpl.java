package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.domain.tables.records.ShopRecord;
import com.rbkmoney.newway.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static com.rbkmoney.newway.domain.Tables.SHOP;

@Component
public class ShopDaoImpl extends AbstractGenericDao implements ShopDao {

    private final RowMapper<Shop> shopRowMapper;

    public ShopDaoImpl(DataSource dataSource) {
        super(dataSource);
        shopRowMapper = new RecordRowMapper<>(SHOP, Shop.class);
    }

    @Override
    public Long save(Shop shop) throws DaoException {
        ShopRecord record = getDslContext().newRecord(SHOP, shop);
        Query query = getDslContext().insertInto(SHOP).set(record)
                .onConflict(SHOP.PARTY_ID, SHOP.SEQUENCE_ID, SHOP.CHANGE_ID)
                .doNothing()
                .returning(SHOP.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Shop get(String partyId, String shopId) throws DaoException {
        Query query = getDslContext().selectFrom(SHOP)
                .where(SHOP.PARTY_ID.eq(partyId).and(SHOP.SHOP_ID.eq(shopId)).and(SHOP.CURRENT));
        return fetchOne(query, shopRowMapper);
    }

    @Override
    public void switchCurrent(String partyId, String shopId) throws DaoException {
        this.getNamedParameterJdbcTemplate().update("update nw.shop set current = false where party_id =:party_id and shop_id =:shop_id and current;" +
                        "update nw.shop set current = true where id = (select max(id) from nw.shop where party_id =:party_id and shop_id =:shop_id);",
                new MapSqlParameterSource("party_id", partyId)
                        .addValue("shop_id", shopId));
    }

    @Override
    public List<Shop> getByPartyId(String partyId) {
        Query query = getDslContext().selectFrom(SHOP)
                .where(SHOP.PARTY_ID.eq(partyId).and(SHOP.CURRENT));
        return fetch(query, shopRowMapper);
    }
}
