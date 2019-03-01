package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IdentityCreatedHandler extends AbstractIdentityHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    private Filter filter;

    public IdentityCreatedHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start identity created handling, eventId={}, identityId={}", event.getId(), event.getSource());
        Identity identity = new Identity();
        identity.setEventId(event.getId());
        identity.setSequenceId(event.getPayload().getSequence());
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        identity.setIdentityId(event.getSource());
        com.rbkmoney.fistful.identity.Identity changeCreated = change.getCreated();
        identity.setPartyId(changeCreated.getParty());
        identity.setPartyContractId(changeCreated.getContract());
        identity.setIdentityClassId(changeCreated.getCls());
        identity.setIdentityProviderId(changeCreated.getProvider());
        identity.setExternalId(changeCreated.getExternalId());
        identity.setIdentityEffectiveChalengeId(changeCreated.getEffectiveChallenge());
        if (changeCreated.isSetBlocked()) {
            identity.setBlocked(changeCreated.isBlocked());
        }
        if (changeCreated.isSetContext()) {
            Map<String, JsonNode> jsonNodeMap = changeCreated.getContext().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
            identity.setContextJson(JsonUtil.objectToJsonString(jsonNodeMap));
        }
        identityDao.save(identity);
        log.info("Identity haven been saved, eventId={}, identityId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
