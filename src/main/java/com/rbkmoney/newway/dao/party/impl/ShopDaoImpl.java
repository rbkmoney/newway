package com.rbkmoney.newway.dao.party.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.mapper.RecordRowMapper;
import com.rbkmoney.newway.dao.party.iface.ShopDao;
import com.rbkmoney.newway.domain.tables.pojos.Shop;
import com.rbkmoney.newway.domain.tables.records.ShopRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.rbkmoney.newway.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.newway.domain.Tables.SHOP;

@Slf4j
@Component
public class ShopDaoImpl extends AbstractGenericDao implements ShopDao {

    private final RowMapper<Shop> shopRowMapper;

    public ShopDaoImpl(DataSource dataSource) {
        super(dataSource);
        shopRowMapper = new RecordRowMapper<>(SHOP, Shop.class);
    }

    @Override
    public Optional<Long> save(Shop shop) throws DaoException {
        ShopRecord record = getDslContext().newRecord(SHOP, shop);
        Query query = getDslContext().insertInto(SHOP).set(record)
                .onConflict(SHOP.PARTY_ID, SHOP.SEQUENCE_ID, SHOP.CHANGE_ID)
                .doNothing()
                .returning(SHOP.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public Shop get(String partyId, String shopId) throws DaoException {
        Query query = getDslContext().selectFrom(SHOP)
                .where(SHOP.PARTY_ID.eq(partyId).and(SHOP.SHOP_ID.eq(shopId)).and(SHOP.CURRENT));
        Shop shop = fetchOne(query, shopRowMapper);
        if (shop == null) {
            throw new NotFoundException(String.format("Shop not found, shopId='%s'", shopId));
        }
        return shop;
    }

    @Override
    public void updateNotCurrent(Long shopId) throws DaoException {
        Query query = getDslContext()
                .update(SHOP).set(SHOP.CURRENT, false)
                .where(SHOP.ID.eq(shopId));
        executeOne(query);
    }

    @Override
    public List<Shop> getByPartyId(String partyId) {
        Query query = getDslContext().selectFrom(SHOP)
                .where(SHOP.PARTY_ID.eq(partyId).and(SHOP.CURRENT));
        return fetch(query, shopRowMapper);
    }

    @Override
    public void saveWithUpdateCurrent(String partyId, Integer changeId, Shop shopSource, String shopId, long sequenceId, Long oldEventId, String eventName) {
        save(shopSource).ifPresentOrElse(
                aLong -> {
                    updateNotCurrent(oldEventId);
                    log.info("Shop {} has been saved, sequenceId={}, partyId={}, shopId={}, changeId={}",
                            eventName, sequenceId, partyId, shopId, changeId);
                },
                () -> log.info("Shop {}} duplicated, sequenceId={}, partyId={}, shopId={}, changeId={}",
                        eventName, sequenceId, partyId, shopId, changeId)
        );
    }
}
