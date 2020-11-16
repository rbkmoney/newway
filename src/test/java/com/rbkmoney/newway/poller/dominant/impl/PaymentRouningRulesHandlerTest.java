package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.newway.dao.dominant.impl.PaymentRoutingRulesDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.PaymentRoutingRule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PaymentRouningRulesHandlerTest {

    @Mock
    private PaymentRoutingRulesDaoImpl paymentRoutingRulesDao;

    @Before
    public void setUp() throws Exception {
        paymentRoutingRulesDao = Mockito.mock(PaymentRoutingRulesDaoImpl.class);
    }

    @Test
    public void convertToDatabaseObjectTest() {
        RoutingRulesObject paymentRoutingRulesObject = buildPaymentRoutingRulesObject();
        PaymentRoutingRulesHandler handler = new PaymentRoutingRulesHandler(paymentRoutingRulesDao);
        handler.setDomainObject(DomainObject.payment_routing_rules(paymentRoutingRulesObject));

        PaymentRoutingRule paymentRoutingRule =
                handler.convertToDatabaseObject(paymentRoutingRulesObject, 1L, true);

        assertNotNull(paymentRoutingRule);
        assertEquals(paymentRoutingRule.getRuleRefId().intValue(), paymentRoutingRulesObject.getRef().getId());
        assertEquals(paymentRoutingRule.getName(), paymentRoutingRulesObject.getData().getName());
        assertEquals(paymentRoutingRule.getDescription(), paymentRoutingRulesObject.getData().getDescription());
        assertFalse(paymentRoutingRule.getRoutingDecisionsJson() == null);
    }

    private RoutingRulesObject buildPaymentRoutingRulesObject() {
        List<RoutingCandidate> candidates = new ArrayList<>();
        Predicate predicate = new Predicate();
        predicate.setConstant(true);
        RoutingCandidate candidate = new RoutingCandidate()
                .setDescription("CN-1")
                .setAllowed(predicate)
                .setTerminal(new TerminalRef().setId(1234))
                .setWeight(12)
                .setPriority(432);
        candidates.add(candidate);
        RoutingDecisions paymentRoutingDecisions = new RoutingDecisions();
        paymentRoutingDecisions.setCandidates(candidates);
        return new RoutingRulesObject()
                .setRef(new RoutingRulesetRef().setId(123))
                .setData(new RoutingRuleset()
                        .setName("test")
                        .setDescription("some desc")
                        .setDecisions(paymentRoutingDecisions)
                );
    }

}
