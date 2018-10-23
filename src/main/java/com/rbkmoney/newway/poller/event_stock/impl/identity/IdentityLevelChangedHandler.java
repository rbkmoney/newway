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
public class IdentityLevelChangedHandler extends AbstractIdentityHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final IdentityDao identityDao;

    private Filter filter;

    public IdentityLevelChangedHandler(IdentityDao identityDao) {
        this.identityDao = identityDao;
        this.filter = new PathConditionFilter(new PathConditionRule("level_changed", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Identity identity = identityDao.get(event.getSource());

        identity.setId(null);
        identity.setWtime(null);
        identity.setEventId(event.getPayload().getId());
        identity.setSequenceId(event.getSequence());
        identity.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        identity.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        identity.setIdentityId(event.getSource());
        identity.setIdentityLevelId(change.getLevelChanged());

        identityDao.updateNotCurrent(event.getSource());
        identityDao.save(identity);

    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
