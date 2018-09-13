package com.rbkmoney.newway.poller.dominant.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.damsel.domain.InspectorObject;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.InspectorDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Inspector;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InspectorHandler extends AbstractDominantHandler<InspectorObject, Inspector, Integer> {

    private final InspectorDaoImpl inspectorDao;

    public InspectorHandler(InspectorDaoImpl inspectorDao) {
        this.inspectorDao = inspectorDao;
    }

    @Override
    protected DomainObjectDao<Inspector, Integer> getDomainObjectDao() {
        return inspectorDao;
    }

    @Override
    protected InspectorObject getObject() {
        return getDomainObject().getInspector();
    }

    @Override
    protected Integer getObjectRefId() {
        return getObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetInspector();
    }

    @Override
    public Inspector convertToDatabaseObject(InspectorObject inspectorObject, Long versionId, boolean current) {
        Inspector inspector = new Inspector();
        inspector.setVersionId(versionId);
        inspector.setInspectorRefId(getObjectRefId());
        com.rbkmoney.damsel.domain.Inspector data = inspectorObject.getData();
        inspector.setName(data.getName());
        inspector.setDescription(data.getDescription());
        inspector.setProxyRefId(data.getProxy().getRef().getId());
        inspector.setProxyAdditionalJson(JsonUtil.objectToJsonString(data.getProxy().getAdditional()));
        inspector.setFallbackRiskScore(data.getFallbackRiskScore().name());
        inspector.setCurrent(current);
        return inspector;
    }
}
