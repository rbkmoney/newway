package com.rbkmoney.newway.handler.dominant.impl;

import com.rbkmoney.damsel.domain.RoutingRulesObject;
import com.rbkmoney.damsel.domain.RoutingRuleset;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.domain.tables.pojos.PaymentRoutingRule;
import com.rbkmoney.newway.handler.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRoutingRulesHandler
        extends AbstractDominantHandler<RoutingRulesObject, PaymentRoutingRule, Integer> {

    private final DomainObjectDao<PaymentRoutingRule, Integer> paymentRoutingRulesDao;

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetRoutingRules();
    }

    @Override
    public PaymentRoutingRule convertToDatabaseObject(RoutingRulesObject rulesObject,
                                                      Long versionId,
                                                      boolean current) {

        PaymentRoutingRule paymentRoutingRule = new PaymentRoutingRule();
        paymentRoutingRule.setRuleRefId(rulesObject.getRef().getId());
        paymentRoutingRule.setVersionId(versionId);

        RoutingRuleset ruleset = rulesObject.getData();
        paymentRoutingRule.setName(ruleset.getName());
        paymentRoutingRule.setDescription(ruleset.getDescription());
        paymentRoutingRule.setRoutingDecisionsJson(JsonUtil.thriftBaseToJsonString(ruleset.getDecisions()));
        paymentRoutingRule.setCurrent(current);
        return paymentRoutingRule;
    }

    @Override
    protected DomainObjectDao<PaymentRoutingRule, Integer> getDomainObjectDao() {
        return paymentRoutingRulesDao;
    }

    @Override
    protected RoutingRulesObject getTargetObject() {
        return getDomainObject().getRoutingRules();
    }

    @Override
    protected Integer getTargetObjectRefId() {
        return getTargetObject().getRef().getId();
    }

}
