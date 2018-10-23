package com.rbkmoney.newway.poller.event_stock.impl.identity;

import com.rbkmoney.fistful.identity.Change;
import com.rbkmoney.fistful.identity.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.domain.tables.pojos.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
        Identity identity = new Identity();
        identity.setEventId(event.getPayload().getId());
        identity.setSequenceId(event.getSequence());
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        identity.setIdentityId(event.getSource());
        identity.setPartyId(change.getCreated().getParty());
        identity.setPartyContractId(change.getCreated().getContract());
        identity.setIdentityClassId(change.getCreated().getCls());
        identity.setIdentityProviderId(change.getCreated().getProvider());

        identityDao.save(identity);
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
