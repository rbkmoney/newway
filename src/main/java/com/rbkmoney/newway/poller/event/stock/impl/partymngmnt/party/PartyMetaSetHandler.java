package com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.party;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyMetaSet;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.domain.tables.pojos.Party;
import com.rbkmoney.newway.poller.event.stock.impl.partymngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.util.JsonUtil;
import com.rbkmoney.newway.util.PartyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("VariableDeclarationUsageDistance")
public class PartyMetaSetHandler extends AbstractPartyManagementHandler {

    private final PartyDao partyDao;

    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "party_meta_set",
            new IsNullCondition().not()));

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(PartyChange change, MachineEvent event, Integer changeId) {
        long sequenceId = event.getEventId();
        PartyMetaSet partyMetaSet = change.getPartyMetaSet();
        String partyId = event.getSourceId();
        log.info("Start party metaset handling, sequenceId={}, partyId={}, changeId={}", sequenceId, partyId, changeId);

        Party partySource = partyDao.get(partyId);
        Long oldId = partySource.getId();
        PartyUtil.resetBaseFields(event, changeId, sequenceId, partySource);
        partySource.setPartyMetaSetNs(partyMetaSet.getNs());
        partySource.setPartyMetaSetDataJson(JsonUtil.thriftBaseToJsonString(partyMetaSet.getData()));

        partyDao.saveWithUpdateCurrent(partySource, oldId, "metaset");
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
