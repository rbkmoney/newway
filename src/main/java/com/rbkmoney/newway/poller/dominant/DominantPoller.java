package com.rbkmoney.newway.poller.dominant;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.RepositorySrv;
import com.rbkmoney.newway.service.DominantService;
import org.apache.thrift.TException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class DominantPoller {

    private List<DominantHandler> handlers;

    private final RepositorySrv.Iface dominantClient;
    private final DominantService dominantService;
    private static final int LIMIT = 10;
    private long after;

    public DominantPoller(RepositorySrv.Iface dominantClient, DominantService dominantService) {
        this.dominantClient = dominantClient;
        this.dominantService = dominantService;
        this.after = dominantService.getLastVersionId().orElse(0L);
    }

    @Scheduled(fixedDelay = 10000)
    public void process() throws TException {
        Map<Long, Commit> pullRange = dominantClient.pullRange(after, LIMIT);
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
