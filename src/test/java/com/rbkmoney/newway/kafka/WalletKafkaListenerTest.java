package com.rbkmoney.newway.kafka;

import com.rbkmoney.newway.poller.listener.WalletKafkaListener;
import com.rbkmoney.newway.serde.WalletChangeMachineEventParser;
import com.rbkmoney.newway.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyList;

@Slf4j
public class WalletKafkaListenerTest extends AbstractKafkaTest {

    @org.springframework.beans.factory.annotation.Value("${kafka.topics.wallet.id}")
    public String topic;

    @MockBean
    WalletService walletService;

    @Test
    public void listenEmptyChanges() {
        sendMessage(topic);
        Mockito.verify(walletService, Mockito.timeout(TimeUnit.MINUTES.toMillis(1)).times(1))
                .handleEvents(anyList());
    }

}
