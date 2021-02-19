package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.ProxyDefinition;
import com.rbkmoney.damsel.domain.ProxyObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.ProxyDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Proxy;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

@Component
public class ProxyHandler extends AbstractDominantHandler<ProxyObject, Proxy, Integer> {

    private final ProxyDaoImpl proxyDao;

    public ProxyHandler(ProxyDaoImpl proxyDao) {
        this.proxyDao = proxyDao;
    }

    @Override
    protected DomainObjectDao<Proxy, Integer> getDomainObjectDao() {
        return proxyDao;
    }

    @Override
    protected ProxyObject getTargetObject() {
        return getDomainObject().getProxy();
    }

    @Override
    protected Integer getTargetObjectRefId() {
        return getTargetObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetProxy();
    }

    @Override
    public Proxy convertToDatabaseObject(ProxyObject proxyObject, Long versionId, boolean current) {
        Proxy proxy = new Proxy();
        proxy.setVersionId(versionId);
        proxy.setProxyRefId(getTargetObjectRefId());
        ProxyDefinition data = proxyObject.getData();
        proxy.setName(data.getName());
        proxy.setDescription(data.getDescription());
        proxy.setUrl(data.getUrl());
        proxy.setOptionsJson(JsonUtil.objectToJsonString(data.getOptions()));
        proxy.setCurrent(current);
        return proxy;
    }
}
