package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.domain.PartyContactInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.config.SerializationConfig;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.poller.event_stock.impl.party_mngmnt.party.PartyCreatedHandler;
import com.rbkmoney.sink.common.serialization.impl.PartyEventDataSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PartyCreatedHandler.class, SerializationConfig.class, PartyManagementService.class})
public class PartyManagementServiceTest {

    @Autowired
    PartyManagementService partyManagementService;

    @MockBean
    PartyDao partyDao;

    @Before
    public void setUp() throws Exception {
        when(partyDao.save(any())).thenReturn(Optional.of(1L));
    }

    @Test
    public void handleEvents() {
        PartyEventDataSerializer partyEventDataSerializer = new PartyEventDataSerializer();

        List<MachineEvent> machineEvents = new ArrayList<>();
        machineEvents.add(createMessage());
        partyManagementService.handleEvents(machineEvents);

        Mockito.verify(partyDao, times(1)).save(any());
    }

    private MachineEvent createMessage() {
        PartyEventDataSerializer partyEventDataSerializer = new PartyEventDataSerializer();

        MachineEvent message = new MachineEvent();
        Value data = new Value();
        PartyEventData partyEventData = new PartyEventData();
        ArrayList<PartyChange> changes = new ArrayList<>();
        PartyChange partyChange = new PartyChange();
        partyChange.setPartyCreated(new PartyCreated()
                .setContactInfo(new PartyContactInfo()
                        .setEmail("test@mail.ru"))
                .setCreatedAt(Instant.now().toString())
                .setId("test"));
        changes.add(partyChange);
        partyEventData.setChanges(changes);
        data.setBin(partyEventDataSerializer.serialize(partyEventData));
        message.setCreatedAt(Instant.now().toString());
        message.setEventId(1L);
        message.setSourceNs("sad");
        message.setSourceId("sda");
        message.setData(data);
        return message;
    }
}