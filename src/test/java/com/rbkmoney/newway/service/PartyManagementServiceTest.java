package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.domain.PartyContactInfo;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyCreated;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.newway.config.SerializationConfig;
import com.rbkmoney.newway.dao.party.iface.PartyDao;
import com.rbkmoney.newway.factory.PartyMachineEventCopyFactoryImpl;
import com.rbkmoney.newway.handler.event.stock.impl.partymngmnt.party.PartyCreatedHandler;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PartyMachineEventCopyFactoryImpl.class,
        PartyCreatedHandler.class, SerializationConfig.class, PartyManagementService.class})
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
        PartyEventDataSerializer partyEventDataSerializer = new PartyEventDataSerializer();
        Value data = new Value();
        data.setBin(partyEventDataSerializer.serialize(partyEventData));
        MachineEvent message = new MachineEvent();
        message.setCreatedAt(Instant.now().toString());
        message.setEventId(1L);
        message.setSourceNs("sad");
        message.setSourceId("sda");
        message.setData(data);
        return message;
    }
}
