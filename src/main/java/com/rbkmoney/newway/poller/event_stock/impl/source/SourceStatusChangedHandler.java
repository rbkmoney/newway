package com.rbkmoney.newway.poller.event_stock.impl.source;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.SinkEvent;
import com.rbkmoney.fistful.source.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
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
public class SourceStatusChangedHandler extends AbstractSourceHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SourceDao sourceDao;

    private final Filter filter;

    public SourceStatusChangedHandler(SourceDao sourceDao) {
        this.sourceDao = sourceDao;
        this.filter = new PathConditionFilter(new PathConditionRule("status.changed", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Status status = change.getStatus().getChanged();
        log.info("Start source status changed handling, eventId={}, sourceId={}, status={}", event.getId(), event.getSource(), status);

        Source source = sourceDao.get(event.getSource());

        source.setId(null);
        source.setWtime(null);
        source.setEventId(event.getId());
        source.setSequenceId(event.getPayload().getSequence());
        source.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        source.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        source.setSourceId(event.getSource());

        source.setSourceStatus(TBaseUtil.unionFieldToEnum(status, SourceStatus.class));

        sourceDao.updateNotCurrent(event.getSource());
        sourceDao.save(source);
        log.info("Source status have been changed, eventId={}, sourceId={}, status={}", event.getId(), event.getSource(), status);
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }

}
