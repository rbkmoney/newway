package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.Operation;
import com.rbkmoney.newway.dao.dominant.iface.DominantDao;
import com.rbkmoney.newway.poller.dominant.DominantHandler;
import com.rbkmoney.newway.poller.dominant.TemporaryDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TemporaryDominantService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DominantDao dominantDao;

    private final List<TemporaryDominantHandler> handlers;

    public TemporaryDominantService(DominantDao dominantDao, List<TemporaryDominantHandler> handlers) {
        this.dominantDao = dominantDao;
        this.handlers = handlers;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void processCommit(long versionId, Map.Entry<Long, Commit> e) {
        List<Operation> operations = e.getValue().getOps();
        operations.forEach(op -> handlers.forEach(h -> {
            if (h.acceptAndSet(op)) {
                log.info("Start to process commit temp with versionId={} operation={} ", versionId, JsonUtil.tBaseToJsonString(op));
                h.handle(op, versionId);
                log.info("End to process commit temp with versionId={}", versionId);
            }
        }));
    }

    public Optional<Long> getLastVersionId() {
        Optional<Long> lastVersionId = Optional.ofNullable(dominantDao.getTemporaryLastVersionId());
        log.info("Last temporary dominant versionId={}", lastVersionId);
        return lastVersionId;
    }
}
