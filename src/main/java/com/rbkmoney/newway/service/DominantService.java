package com.rbkmoney.newway.service;

import com.rbkmoney.newway.dao.dominant.iface.DominantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DominantService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DominantDao dominantDao;

    public DominantService(DominantDao dominantDao) {
        this.dominantDao = dominantDao;
    }

    public Optional<Long> getLastVersionId() {
        Optional<Long> lastVersionId = Optional.ofNullable(dominantDao.getLastVersionId());
        log.info("Last dominant versionId={}", lastVersionId);
        return lastVersionId;
    }
}
