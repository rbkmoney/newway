package com.rbkmoney.newway.handler.event.stock.impl.source;

import com.rbkmoney.fistful.source.Change;
import com.rbkmoney.fistful.source.Status;
import com.rbkmoney.fistful.source.TimestampedChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.domain.enums.SourceStatus;
import com.rbkmoney.newway.domain.tables.pojos.Source;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceStatusChangedHandler implements SourceHandler {

    private final SourceDao sourceDao;
    private final MachineEventCopyFactory<Source, String> sourceMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.status.status", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Status status = change.getStatus().getStatus();
        long sequenceId = event.getEventId();
        String sourceId = event.getSourceId();
        log.info("Start source status changed handling, sequenceId={}, sourceId={}", sequenceId, sourceId);

        final Source sourceOld = sourceDao.get(sourceId);
        Source sourceNew = sourceMachineEventCopyFactory
                .create(event, sequenceId, sourceId, sourceOld, timestampedChange.getOccuredAt());

        sourceNew.setSourceStatus(TBaseUtil.unionFieldToEnum(status, SourceStatus.class));

        sourceDao.save(sourceNew).ifPresentOrElse(
                id -> {
                    sourceDao.updateNotCurrent(sourceOld.getId());
                    log.info("Source status have been changed, sequenceId={}, sourceId={}", sequenceId, sourceId);
                },
                () -> log.info("Source status bound duplicated, sequenceId={}, sourceId={}", sequenceId, sourceId));
    }

}
