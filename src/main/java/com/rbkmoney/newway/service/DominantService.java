package com.rbkmoney.newway.service;

import com.rbkmoney.damsel.domain_config.Commit;
import com.rbkmoney.damsel.domain_config.Operation;
import com.rbkmoney.newway.dao.dominant.iface.DominantDao;
import com.rbkmoney.newway.poller.dominant.DominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DominantService {

    private final DominantDao dominantDao;

    private final List<DominantHandler> handlers;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processCommit(long versionId, Map.Entry<Long, Commit> entry) {
        List<Operation> operations = entry.getValue().getOps();
        operations.forEach(operation -> handlers.forEach(handler -> {
            if (handler.acceptAndSet(operation)) {
                processOperation(handler, operation, versionId);
            }
        }));
    }

    private void processOperation(DominantHandler handler, Operation operation, Long versionId) {
        try {
            log.info("Start to process commit with versionId={} operation={} ",
                    versionId, JsonUtil.tBaseToJsonString(operation));
            handler.handle(operation, versionId);
            log.info("End to process commit with versionId={}", versionId);
        } catch (Exception ex) {
            log.error("The error was received when the service processed operation", ex);
            throw ex;
        }
    }

    public Optional<Long> getLastVersionId() {
        Optional<Long> lastVersionId = Optional.ofNullable(dominantDao.getLastVersionId());
        log.info("Last dominant versionId={}", lastVersionId);
        return lastVersionId;
    }
}
