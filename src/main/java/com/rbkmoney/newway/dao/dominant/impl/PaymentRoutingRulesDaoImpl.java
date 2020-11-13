package com.rbkmoney.newway.dao.dominant.impl;

import com.rbkmoney.dao.impl.AbstractGenericDao;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.PaymentRoutingRule;
import com.rbkmoney.newway.domain.tables.records.PaymentRoutingRuleRecord;
import com.rbkmoney.newway.exception.DaoException;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Query;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import static com.rbkmoney.newway.domain.tables.PaymentRoutingRule.PAYMENT_ROUTING_RULE;

@Component
public class PaymentRoutingRulesDaoImpl extends AbstractGenericDao
        implements DomainObjectDao<PaymentRoutingRule, Integer> {

    public PaymentRoutingRulesDaoImpl(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long save(PaymentRoutingRule routingRule) throws DaoException {
        PaymentRoutingRuleRecord record = getDslContext().newRecord(PAYMENT_ROUTING_RULE, routingRule);
        Query query = getDslContext()
                .insertInto(PAYMENT_ROUTING_RULE)
                .set(record)
                .returning(PAYMENT_ROUTING_RULE.ID);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        executeOne(query, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateNotCurrent(Integer ruleId) throws DaoException {
        Query query = getDslContext()
                .update(PAYMENT_ROUTING_RULE)
                .set(PAYMENT_ROUTING_RULE.CURRENT, false)
                .where(PAYMENT_ROUTING_RULE.RULE_ID.eq(ruleId).and(PAYMENT_ROUTING_RULE.CURRENT));
        execute(query);
    }
}
