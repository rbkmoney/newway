package com.rbkmoney.newway.poller.dominant;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.Operation;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class DominantProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<DominantHandler> handlers;

    public DominantProcessor(List<DominantHandler> handlers) {
        this.handlers = handlers;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void processCommit(long versionId, Map.Entry<Long, Commit> e) {
        List<Operation> operations = e.getValue().getOps();
        operations.forEach(op -> handlers.forEach(h -> {
            if (h.accept(op)) {
                log.info("Start to process commit with versionId={} operation={} ", versionId, JsonUtil.tBaseToJsonString(op));
                h.handle(op, versionId);
                log.info("End to process commit with versionId={}", versionId);
            }
        }));
    }

}
