package com.rbkmoney.newway.poller.dominant;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.Operation;
import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.newway.service.DominantService;
import com.rbkmoney.newway.util.JsonUtil;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
@DependsOn("dbInitializer")
public class DominantPoller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RepositorySrv.Iface dominantClient;
    private final DominantProcessor dominantProcessor;
    private final int maxQuerySize;
    private long after;

    public DominantPoller(RepositorySrv.Iface dominantClient, DominantProcessor dominantProcessor,
                          DominantService dominantService, @Value("${dmt.polling.maxQuerySize}") int maxQuerySize) {
        this.dominantClient = dominantClient;
        this.dominantProcessor = dominantProcessor;
        this.after = dominantService.getLastVersionId().orElse(0L);
        this.maxQuerySize = maxQuerySize;
    }

    @Scheduled(fixedDelayString = "${dmt.polling.delay}")
    public void process() {
        Map<Long, Commit> pullRange;
        final AtomicLong versionId = new AtomicLong();
        try {
            pullRange = dominantClient.pullRange(after, maxQuerySize);
            pullRange.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e -> {
                try {
                    versionId.set(e.getKey());
                    dominantProcessor.processCommit(versionId.get(), e);
                    after = versionId.get();
                } catch (RuntimeException ex) {
                    throw new RuntimeException(String.format("Unexpected error when polling dominant, versionId=%d, pullRange=%s",
                            versionId.get(), pullRange.toString()), ex);
                }
            });
        } catch (TException e) {
            log.warn("Error to polling dominant, after={}", after, e);
        }
    }
}
