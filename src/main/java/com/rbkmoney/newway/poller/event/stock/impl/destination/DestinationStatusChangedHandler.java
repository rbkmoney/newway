package com.rbkmoney.newway.poller.event.stock.impl.destination;

import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.Status;
import com.rbkmoney.fistful.destination.TimestampedChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.enums.DestinationStatus;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DestinationStatusChangedHandler implements DestinationHandler {

    private final DestinationDao destinationDao;
    private final MachineEventCopyFactory<Destination, String> destinationMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.status.changed", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        Status status = change.getStatus().getChanged();
        long sequenceId = event.getEventId();
        String destinationId = event.getSourceId();
        log.info("Start destination status changed handling, sequenceId={}, destinationId={}", sequenceId,
                destinationId);

        final Destination destinationOld = destinationDao.get(destinationId);
        Destination destinationNew = destinationMachineEventCopyFactory
                .create(event, sequenceId, destinationId, destinationOld, timestampedChange.getOccuredAt());

        destinationNew.setDestinationStatus(TBaseUtil.unionFieldToEnum(status, DestinationStatus.class));

        destinationDao.save(destinationNew).ifPresentOrElse(
                id -> {
                    destinationDao.updateNotCurrent(destinationOld.getId());
                    log.info("Destination status have been changed, sequenceId={}, destinationId={}", sequenceId,
                            destinationId);
                },
                () -> log
                        .info("Destination have been saved, sequenceId={}, destinationId={}", sequenceId, destinationId)
        );
    }

}
