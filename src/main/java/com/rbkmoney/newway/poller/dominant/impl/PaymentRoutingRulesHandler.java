package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.RoutingRulesObject;
import com.rbkmoney.damsel.domain.RoutingRuleset;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.PaymentRoutingRule;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRoutingRulesHandler
        extends AbstractDominantHandler<RoutingRulesObject, PaymentRoutingRule, Integer> {

    private final DomainObjectDao<PaymentRoutingRule, Integer> paymentRoutingRulesDao;

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetPaymentRoutingRules();
    }

    @Override
    public PaymentRoutingRule convertToDatabaseObject(RoutingRulesObject rulesObject,
                                                      Long versionId,
                                                      boolean current) {
        PaymentRoutingRule paymentRoutingRule = new PaymentRoutingRule();
        paymentRoutingRule.setRuleId(rulesObject.getRef().getId());

        RoutingRuleset ruleset = rulesObject.getData();
        paymentRoutingRule.setName(ruleset.getName());
        paymentRoutingRule.setDescription(ruleset.getDescription());
        paymentRoutingRule.setRoutingDecisionsJson(JsonUtil.tBaseToJsonString(ruleset.getDecisions()));
        paymentRoutingRule.setCurrent(current);
        return paymentRoutingRule;
    }

    @Override
    protected DomainObjectDao<PaymentRoutingRule, Integer> getDomainObjectDao() {
        return paymentRoutingRulesDao;
    }

    @Override
    protected RoutingRulesObject getObject() {
        return getDomainObject().getPaymentRoutingRules();
    }

    @Override
    protected Integer getObjectRefId() {
        return getObject().getRef().getId();
    }

}
