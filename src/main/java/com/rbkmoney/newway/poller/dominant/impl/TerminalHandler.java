package com.rbkmoney.newway.poller.dominant.impl;

import com.rbkmoney.damsel.domain.TerminalObject;
import com.rbkmoney.newway.dao.dominant.iface.DomainObjectDao;
import com.rbkmoney.newway.dao.dominant.impl.TerminalDaoImpl;
import com.rbkmoney.newway.domain.tables.pojos.Terminal;
import com.rbkmoney.newway.poller.dominant.AbstractDominantHandler;
import com.rbkmoney.newway.util.JsonUtil;
import org.springframework.stereotype.Component;

@Component
public class TerminalHandler extends AbstractDominantHandler<TerminalObject, Terminal, Integer> {

    private final TerminalDaoImpl terminalDao;

    public TerminalHandler(TerminalDaoImpl terminalDao) {
        this.terminalDao = terminalDao;
    }

    @Override
    protected DomainObjectDao<Terminal, Integer> getDomainObjectDao() {
        return terminalDao;
    }

    @Override
    protected TerminalObject getObject() {
        return getDomainObject().getTerminal();
    }

    @Override
    protected Integer getObjectRefId() {
        return getObject().getRef().getId();
    }

    @Override
    protected boolean acceptDomainObject() {
        return getDomainObject().isSetTerminal();
    }

    @Override
    public Terminal convertToDatabaseObject(TerminalObject terminalObject, Long versionId, boolean current) {
        Terminal terminal = new Terminal();
        terminal.setVersionId(versionId);
        terminal.setTerminalRefId(getObjectRefId());
        com.rbkmoney.damsel.domain.Terminal data = terminalObject.getData();
        terminal.setName(data.getName());
        terminal.setDescription(data.getDescription());
        if (data.isSetOptions()) {
            terminal.setOptionsJson(JsonUtil.objectToJsonString(data.getOptions()));
        }
        terminal.setRiskCoverage(data.getRiskCoverage().name());
        if (data.isSetTerms()) {
            terminal.setTermsJson(JsonUtil.tBaseToJsonString(data.getTerms()));
        }
        terminal.setExternalTerminalId(data.getExternalTerminalId());
        terminal.setExternalMerchantId(data.getExternalMerchantId());
        terminal.setMcc(data.getMcc());
        if (data.isSetProviderRef()) {
            terminal.setTerminalProviderRefId(data.getProviderRef().getId());
        }
        terminal.setCurrent(current);
        return terminal;
    }
}
