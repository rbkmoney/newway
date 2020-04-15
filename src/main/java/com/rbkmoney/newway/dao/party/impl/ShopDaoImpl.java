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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .onConflict(SHOP.PARTY_ID, SHOP.SEQUENCE_ID, SHOP.CHANGE_ID, SHOP.CLAIM_EFFECT_ID, SHOP.REVISION)
                .doNothing()
                .returning(SHOP.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        execute(query, keyHolder);
        return Optional.ofNullable(keyHolder.getKey()).map(Number::longValue);
    }

    @Override
    public void saveBatch(List<Shop> shops) throws DaoException {
        List<Query> queries = shops.stream()
                .map(contractor -> getDslContext().newRecord(SHOP, contractor))
                .map(contractorRecord -> getDslContext().insertInto(SHOP)
                        .set(contractorRecord)
                        .onConflict(SHOP.PARTY_ID, SHOP.SEQUENCE_ID, SHOP.CHANGE_ID, SHOP.CLAIM_EFFECT_ID, SHOP.REVISION)
                        .doNothing()
                )
                .collect(Collectors.toList());
        batchExecute(queries);
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
    public void updateNotCurrent(Long id) throws DaoException {
        Query query = getDslContext()
                .update(SHOP).set(SHOP.CURRENT, false)
                .where(SHOP.ID.eq(id));
        executeOne(query);
    }

    @Override
    public void updateNotCurrent(List<Long> ids) throws DaoException {
        Query query = getDslContext().update(SHOP).set(SHOP.CURRENT, false).where(SHOP.ID.in(ids));
        execute(query);
    }

    @Override
    public void switchCurrent(List<Long> ids) throws DaoException {
        ids.forEach(id ->
                this.getNamedParameterJdbcTemplate().update("update nw.shop set current = false where id =:id and current;" +
                                "update nw.shop set current = true where id = (select max(id) from nw.shop where id =:id);",
                        new MapSqlParameterSource("id", id)));
    }

    @Override
    public List<Shop> getByPartyId(String partyId) {
        Query query = getDslContext().selectFrom(SHOP)
                .where(SHOP.PARTY_ID.eq(partyId).and(SHOP.CURRENT));
        return fetch(query, shopRowMapper);
    }

    @Override
    public void saveWithUpdateCurrent(Shop shopSource, Long oldEventId, String eventName) {
        save(shopSource).ifPresentOrElse(
                aLong -> {
                    updateNotCurrent(oldEventId);
                    log.info("Shop {} has been saved, sequenceId={}, partyId={}, shopId={}, changeId={}",
                            eventName, shopSource.getSequenceId(), shopSource.getPartyId(), shopSource.getShopId(), shopSource.getChangeId());
                },
                () -> log.info("Shop {}} duplicated, sequenceId={}, partyId={}, shopId={}, changeId={}",
                        eventName, shopSource.getSequenceId(), shopSource.getPartyId(), shopSource.getShopId(), shopSource.getChangeId())
        );
    }
}
