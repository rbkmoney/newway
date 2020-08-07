package com.rbkmoney.newway.poller.event_stock.impl.source;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.Internal;
import com.rbkmoney.fistful.source.Resource;
import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.enums.SourceStatus;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceCreatedHandler extends AbstractSourceHandler {

    private final SourceDao sourceDao;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String sourceId = event.getSourceId();
        log.info("Start source created handling, sequenceId={}, sourceId={}", sequenceId, sourceId);
        Source source = new Source();

        initDefaultFields(event, (int) sequenceId, sourceId, source, timestampedChange.getOccuredAt());

        source.setSourceName(change.getCreated().getName());
        source.setSourceStatus(SourceStatus.unauthorized);
        source.setExternalId(change.getCreated().getExternalId());

        Resource resource = change.getCreated().getResource();
        if (resource.isSetInternal()) {
            Internal internal = resource.getInternal();
            source.setResourceInternalDetails(internal.getDetails());
        }

        sourceDao.save(source).ifPresentOrElse(
                dbContractId -> log.info("Source created has been saved, sequenceId={}, sourceId={}", sequenceId, sourceId),
                () -> log.info("Source created bound duplicated,, sequenceId={}, sourceId={}", sequenceId, sourceId));
    }

}
