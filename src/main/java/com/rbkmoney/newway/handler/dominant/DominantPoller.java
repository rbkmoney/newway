package com.rbkmoney.newway.handler.dominant;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.newway.service.DominantService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@DependsOn("flywayInitializer")
public class DominantPoller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RepositorySrv.Iface dominantClient;
    private final DominantService dominantService;
    private final int maxQuerySize;
    private final boolean pollingEnabled;
    private long after;

    public DominantPoller(RepositorySrv.Iface dominantClient,
                          DominantService dominantService,
                          int maxQuerySize,
                          boolean pollingEnabled) {
        this.dominantClient = dominantClient;
        this.dominantService = dominantService;
        this.after = dominantService.getLastVersionId().orElse(0L);
        this.maxQuerySize = maxQuerySize;
        this.pollingEnabled = pollingEnabled;
    }

    @Scheduled(fixedDelayString = "${dmt.polling.delay}")
    public void process() {
        if (pollingEnabled) {
            Map<Long, Commit> pullRange;
            final AtomicLong versionId = new AtomicLong();
            try {
                pullRange = dominantClient.pullRange(after, maxQuerySize);
                pullRange.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e -> {
                    try {
                        versionId.set(e.getKey());
                        dominantService.processCommit(versionId.get(), e);
                        after = versionId.get();
                    } catch (RuntimeException ex) {
                        throw new RuntimeException(
                                String.format("Unexpected error when polling dominant, versionId=%d", versionId.get()),
                                ex);
                    }
                });
            } catch (TException e) {
                log.warn("Error to polling dominant, after={}", after, e);
            }
        }
    }
}
