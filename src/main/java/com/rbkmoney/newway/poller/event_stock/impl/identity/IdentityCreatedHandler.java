package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityCreatedHandler extends AbstractIdentityHandler {

    private final IdentityDao identityDao;

    @Getter
    private Filter filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String identityId = event.getSourceId();
        log.info("Start identity created handling, sequenceId={}, identityId={}", sequenceId, identityId);
        Identity identity = new Identity();
        identity.setIdentityId(identityId);
        identity.setSequenceId((int) sequenceId);
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(timestampedChange.getOccuredAt()));
        com.rbkmoney.fistful.identity.Identity changeCreated = change.getCreated();
        identity.setPartyId(changeCreated.getParty());
        identity.setPartyContractId(changeCreated.getContract());
        identity.setIdentityClassId(changeCreated.getCls());
        identity.setIdentityProviderId(changeCreated.getProvider());
        identity.setExternalId(changeCreated.getExternalId());
        if (changeCreated.isSetMetadata()) {
            Map<String, JsonNode> jsonNodeMap = changeCreated.getMetadata().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
            identity.setContextJson(JsonUtil.objectToJsonString(jsonNodeMap));
        }
        identityDao.save(identity).ifPresentOrElse(
                id -> log.info("Identity haven been saved, sequenceId={}, identityId={}", sequenceId, identityId),
                () -> log.info("Identity created bound duplicated, sequenceId={}, identityId={}", sequenceId, identityId)
        );
    }

}
