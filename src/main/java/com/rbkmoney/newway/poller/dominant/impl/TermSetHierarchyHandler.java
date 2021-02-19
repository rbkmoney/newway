package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.TermSetHierarchyObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.TermSetHierarchyDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.TermSetHierarchy;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TermSetHierarchyHandler extends AbstractDominantHandler<TermSetHierarchyObject, TermSetHierarchy, Integer> {

    private final TermSetHierarchyDaoImpl termSetHierarchyDao;

    public TermSetHierarchyHandler(TermSetHierarchyDaoImpl termSetHierarchyDao) {
        this.termSetHierarchyDao = termSetHierarchyDao;
    }

    @Override
    protected DomainObjectDao<TermSetHierarchy, Integer> getDomainObjectDao() {
        return termSetHierarchyDao;
    }

    @Override
    protected TermSetHierarchyObject getTargetObject() {
        return getDomainObject().getTermSetHierarchy();
    }

    @Override
    protected Integer getTargetObjectRefId() {
        return getTargetObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetTermSetHierarchy();
    }

    @Override
    public TermSetHierarchy convertToDatabaseObject(TermSetHierarchyObject termSetHierarchyObject, Long versionId, boolean current) {
        TermSetHierarchy termSetHierarchy = new TermSetHierarchy();
        termSetHierarchy.setVersionId(versionId);
        termSetHierarchy.setTermSetHierarchyRefId(getTargetObjectRefId());
        com.rbkmoney.damsel.domain.TermSetHierarchy data = termSetHierarchyObject.getData();
        termSetHierarchy.setName(data.getName());
        termSetHierarchy.setDescription(data.getDescription());
        if (data.isSetParentTerms()) {
            termSetHierarchy.setParentTermsRefId(data.getParentTerms().getId());
        }
        termSetHierarchy.setTermSetsJson(JsonUtil.objectToJsonString(data.getTermSets().stream().map(JsonUtil::tBaseToJsonNode).collect(Collectors.toList())));
        termSetHierarchy.setCurrent(current);
        return termSetHierarchy;
    }
}
