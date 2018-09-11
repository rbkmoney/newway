package com.rbkmoney.newway.poller.dominant;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.newway.service.DominantService;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class DominantPoller {

    private final List<DominantHandler> handlers;
    private final RepositorySrv.Iface dominantClient;
    private final int maxQuerySize;
    private long after;

    public DominantPoller(List<DominantHandler> handlers, RepositorySrv.Iface dominantClient,
                          DominantService dominantService, @Value("${dmt.polling.maxQuerySize}") int maxQuerySize) {
        this.handlers = handlers;
        this.dominantClient = dominantClient;
        this.after = dominantService.getLastVersionId().orElse(0L);
        this.maxQuerySize = maxQuerySize;
    }

    @Scheduled(fixedDelay = 10000)
    public void process() throws TException {
        Map<Long, Commit> pullRange = dominantClient.pullRange(after, maxQuerySize);
        pullRange.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(e -> {
            e.getValue().getOps().forEach(op -> handlers.forEach(h -> {
                if (h.accept(op)) {
                    h.handle(op, e.getKey());
                }
            }));
            after = e.getKey();
        });
    }
}
