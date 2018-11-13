package com.rbkmoney.newway.poller.event_stock.impl.destination;

import com.rbkmoney.fistful.destination.Change;
import com.rbkmoney.fistful.destination.SinkEvent;
import com.rbkmoney.fistful.destination.Status;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.domain.enums.DestinationStatus;
import com.rbkmoney.newway.domain.tables.pojos.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DestinationStatusChangedHandler extends AbstractDestinationHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DestinationDao destinationDao;

    private final Filter filter;

    public DestinationStatusChangedHandler(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
        this.filter = new PathConditionFilter(new PathConditionRule("status.changed", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        Status status = change.getStatus().getChanged();
        log.info("Start destination status changed handling, eventId={}, destinationId={}, status={}", event.getId(), event.getSource(), status);

        Destination destination = destinationDao.get(event.getSource());

        destination.setId(null);
        destination.setWtime(null);
        destination.setEventId(event.getId());
        destination.setSequenceId(event.getPayload().getSequence());
        destination.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        destination.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        destination.setDestinationId(event.getSource());

        destination.setDestinationStatus(TBaseUtil.unionFieldToEnum(status, DestinationStatus.class));

        destinationDao.updateNotCurrent(event.getSource());
        destinationDao.save(destination);
        log.info("Destination status have been changed, eventId={}, destinationId={}, status={}", event.getId(), event.getSource(), status);
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
