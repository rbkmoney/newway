package com.rbkmoney.newway.poller.dominant;

import com.rbkmoney.damsel.domain.DomainObject;
import com.rbkmoney.damsel.domain_config.Operation;

public abstract class AbstractDominantHandler implements DominantHandler<Operation> {

    private DomainObject domainObject;

    @Override
    public void handle(Operation operation, Long versionId) {
        if (operation.isSetInsert()) {
            insertDomainObject(domainObject, versionId);
        } else if (operation.isSetUpdate()) {
            updateDomainObject(domainObject, versionId);
        } else if (operation.isSetRemove()) {
            removeDomainObject(domainObject, versionId);
        } else {
            throw new IllegalStateException("Unknown type of operation. Only insert/update/remove supports. Operation: " + operation);
        }
    }

    @Override
    public boolean accept(Operation operation) {
        if (operation.isSetInsert()) {
            domainObject = operation.getInsert().getObject();
        } else if (operation.isSetUpdate()) {
            domainObject = operation.getUpdate().getNewObject();
        } else if (operation.isSetRemove()) {
            domainObject = operation.getRemove().getObject();
        } else {
            throw new IllegalStateException("Unknown type of operation. Only insert/update/remove supports. Operation: " + operation);
        }
        return acceptDomainObject(domainObject);
    }

    protected abstract boolean acceptDomainObject(DomainObject domainObject);
    protected abstract void insertDomainObject(DomainObject domainObject, Long versionId);
    protected abstract void updateDomainObject(DomainObject domainObject, Long versionId);
    protected abstract void removeDomainObject(DomainObject domainObject, Long versionId);
}
