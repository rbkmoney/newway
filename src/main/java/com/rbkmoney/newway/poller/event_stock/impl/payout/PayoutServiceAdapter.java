package com.rbkmoney.newway.poller.event_stock.impl.payout;

import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventRange;
import com.rbkmoney.damsel.payout_processing.EventSinkSrv;
import com.rbkmoney.damsel.payout_processing.NoLastEvent;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.poll.ServiceAdapter;
import com.rbkmoney.eventstock.client.poll.ServiceException;
import com.rbkmoney.eventstock.client.poll.UnsupportedByServiceException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.woody.api.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PayoutServiceAdapter implements ServiceAdapter<Event, EventConstraint> {

    private final EventSinkSrv.Iface repository;

    public static PayoutServiceAdapter build(ClientBuilder clientBuilder) {
        return new PayoutServiceAdapter(clientBuilder.build(EventSinkSrv.Iface.class));
    }

    public PayoutServiceAdapter(EventSinkSrv.Iface repository) {
        this.repository = repository;
    }

    @Override
    public Collection<Event> getEventRange(com.rbkmoney.eventstock.client.EventConstraint srcConstraint, int limit) throws ServiceException {
        EventRange eventRange = convertConstraint(srcConstraint, limit);
        log.debug("New event range request: {}, limit: {}", eventRange, limit);
        try {
            Collection<Event> events = repository.getEvents(eventRange).stream().map(this::toEvent).collect(Collectors.toList());
            log.debug("Received events count: {}", events.size());
            log.trace("Received events: {}", events);
            return events;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Event getFirstEvent() throws ServiceException {
        try {
            log.debug("New first event request");
            EventRange range = new EventRange();
            range.setLimit(1);
            List<Event> events = repository.getEvents(range);
            if (events.isEmpty()) {
                return null;
            }
            Event event = toEvent(events.get(0));
            log.debug("Received event: {}", event);
            return event;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Event getLastEvent() throws ServiceException {
        try {
            log.debug("New last event request");
            long lastId = repository.getLastEventID();
            EventRange range = new EventRange();
            if (lastId > Long.MIN_VALUE) {
                range.setAfter(lastId - 1);
            }
            range.setLimit(1);
            List<Event> events = repository.getEvents(range);
            if (events.isEmpty()) {
                return null;
            }
            Event event = toEvent(events.get(0));
            log.debug("Received event: {}", event);
            return event;
        } catch (NoLastEvent e) {
            return null;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Long getEventId(Event event) {
        return event.getId();
    }

    public TemporalAccessor getEventCreatedAt(Event event) {
        return TypeUtil.stringToTemporal(event.getCreatedAt());
    }

    private Event toEvent(Event event) {
        Event current = new Event();
        current.setId(event.getId());
        current.setPayload(event.getPayload());
        current.setSource(event.getSource());
        current.setCreatedAt(event.getCreatedAt());
        return current;
    }

    public static EventRange convertConstraint(com.rbkmoney.eventstock.client.EventConstraint scrConstraint, int limit) throws UnsupportedByServiceException {
        if (scrConstraint.getIdRange() != null) {
            EventRange range = convertRange(scrConstraint.getIdRange());
            range.setLimit(limit);
            return range;
        } else if (scrConstraint.getTimeRange() != null) {
            throw new UnsupportedByServiceException("Time range is not supported by Payout interface");
        }
        throw new UnsupportedByServiceException("Unexpected constraint range type: " + scrConstraint);
    }

    private static EventRange convertRange(com.rbkmoney.eventstock.client.EventConstraint.EventIDRange srcIdRange) throws UnsupportedByServiceException {
        EventRange resIdRange = new EventRange();

        if (srcIdRange.isFromDefined()) {
            if(srcIdRange.isFromInclusive()) {
                if (srcIdRange.getFrom() > Long.MIN_VALUE) {
                    resIdRange.setAfter(srcIdRange.getFrom() - 1);//Based on Andrew confirmation that mg doesn't conform to api (no exception thrown on unknown event). Otherwise it's not possible to get specific event by id if preceding gap exists
                }
            } else {
                resIdRange.setAfter(srcIdRange.getFrom());
            }
        }
        if (srcIdRange.isToDefined()) {
            throw new UnsupportedByServiceException("Right Id bound is not supported by Payout interface");
        }
        return resIdRange;
    }
}
