package com.rbkmoney.newway.poller.event_stock.impl.source;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.Internal;
import com.rbkmoney.fistful.source.Resource;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.enums.SourceStatus;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SourceCreatedHandler extends AbstractSourceHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SourceDao sourceDao;

    private final Filter filter;

    public SourceCreatedHandler(SourceDao sourceDao) {
        this.sourceDao = sourceDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start source created handling, eventId={}, sourceId={}", event.getId(), event.getSource());
        Source source = new Source();
        source.setEventId(event.getId());
        source.setSequenceId(event.getPayload().getSequence());
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        source.setSourceId(event.getSource());
        source.setSourceName(change.getCreated().getName());
        source.setSourceStatus(SourceStatus.unauthorized);
        source.setExternalId(change.getCreated().getExternalId());

        Resource resource = change.getCreated().getResource();
        if (resource.isSetInternal()) {
            Internal internal = resource.getInternal();
            source.setResourceInternalDetails(internal.getDetails());
        }

        sourceDao.updateNotCurrent(event.getSource());
        sourceDao.save(source);
        log.info("Source have been saved, eventId={}, sourceId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
