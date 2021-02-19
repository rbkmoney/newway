package com.rbkmoney.newway.poller.dominant;

import com.rbkmoney.damsel.domain.DomainObject;
import com.rbkmoney.damsel.domain_config.Operation;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @param <T> - damsel object class (CategoryObject, CurrencyObject etc.)
 * @param <C> - jooq object class (Category, Currency etc.)
 * @param <I> - object reference id class (Integer, String etc.)
 */
public abstract class AbstractDominantHandler<T, C, I> implements DominantHandler<Operation> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DomainObject domainObject;

    public DomainObject getDomainObject() {
        return domainObject;
    }

    public void setDomainObject(DomainObject domainObject) {
        this.domainObject = domainObject;
    }

    protected abstract DomainObjectDao<C, I> getDomainObjectDao();
    protected abstract T getTargetObject();
    protected abstract I getTargetObjectRefId();
    protected abstract boolean acceptDomainObject();
    public abstract C convertToDatabaseObject(T object, Long versionId, boolean current);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(Operation operation, Long versionId) {
        T object = getTargetObject();
        if (operation.isSetInsert()) {
            insertDomainObject(object, versionId);
        } else if (operation.isSetUpdate()) {
            updateDomainObject(object, versionId);
        } else if (operation.isSetRemove()) {
            removeDomainObject(object, versionId);
        } else {
            throw new IllegalStateException("Unknown type of operation. Only insert/update/remove supports. Operation: " + operation);
        }
    }

    @Override
    public boolean acceptAndSet(Operation operation) {
        if (operation.isSetInsert()) {
            setDomainObject(operation.getInsert().getObject());
        } else if (operation.isSetUpdate()) {
            setDomainObject(operation.getUpdate().getNewObject());
        } else if (operation.isSetRemove()) {
            setDomainObject(operation.getRemove().getObject());
        } else {
            throw new IllegalStateException("Unknown type of operation. Only insert/update/remove supports. Operation: " + operation);
        }
        return acceptDomainObject();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertDomainObject(T object, Long versionId) {
        log.info("Start to insert '{}' with id={}, versionId={}", object.getClass().getSimpleName(), getTargetObjectRefId(), versionId);
        getDomainObjectDao().save(convertToDatabaseObject(object, versionId, true));
        log.info("End to insert '{}' with id={}, versionId={}", object.getClass().getSimpleName(), getTargetObjectRefId(), versionId);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateDomainObject(T object, Long versionId) {
        log.info("Start to update '{}' with id={}, versionId={}", object.getClass().getSimpleName(), getTargetObjectRefId(), versionId);
        getDomainObjectDao().updateNotCurrent(getTargetObjectRefId());
        getDomainObjectDao().save(convertToDatabaseObject(object, versionId, true));
        log.info("End to update '{}' with id={}, versionId={}", object.getClass().getSimpleName(), getTargetObjectRefId(), versionId);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeDomainObject(T object, Long versionId) {
        log.info("Start to remove '{}' with id={}, versionId={}", object.getClass().getSimpleName(), getTargetObjectRefId(), versionId);
        getDomainObjectDao().updateNotCurrent(getTargetObjectRefId());
        getDomainObjectDao().save(convertToDatabaseObject(object, versionId, false));
        log.info("End to remove '{}' with id={}, versionId={}", object.getClass().getSimpleName(), getTargetObjectRefId(), versionId);
    }
}
