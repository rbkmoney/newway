package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.contract;

import com.rbkmoney.damsel.domain.ContractStatus;
import com.rbkmoney.damsel.payment_processing.ContractEffectUnit;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.party.iface.ContractAdjustmentDao;
import com.rbkmoney.newway.dao.party.iface.ContractDao;
import com.rbkmoney.newway.dao.party.iface.PayoutToolDao;
import com.rbkmoney.newway.domain.enums.Contractstatus;
import com.rbkmoney.newway.domain.tables.pojos.Contract;
import com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment;
import com.rbkmoney.newway.domain.tables.pojos.PayoutTool;
import com.rbkmoney.newway.exception.NotFoundException;
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractStatusChangedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;

    private final Filter filter;

    public ContractStatusChangedHandler(ContractDao contractDao, ContractAdjustmentDao contractAdjustmentDao, PayoutToolDao payoutToolDao) {
        this.contractDao = contractDao;
        this.contractAdjustmentDao = contractAdjustmentDao;
        this.payoutToolDao = payoutToolDao;
        this.filter = new PathConditionFilter(new PathConditionRule(
                "claim_created.status.accepted",
                new IsNullCondition().not()));
    }

    @Override
    public void handle(PartyChange change, Event event) {
        long eventId = event.getId();
        change.getClaimCreated().getStatus().getAccepted().getEffects().stream()
                .filter(e -> e.isSetContractEffect() && e.getContractEffect().getEffect().isSetStatusChanged()).forEach(e -> {
            ContractEffectUnit contractEffectUnit = e.getContractEffect();
            ContractStatus statusChanged = contractEffectUnit.getEffect().getStatusChanged();
            String contractId = contractEffectUnit.getContractId();
            String partyId = event.getSource().getPartyId();
            log.info("Start contractSource status changed handling, eventId={}, partyId={}, contractId={}", eventId, partyId, contractId);
            Contract contractSource = contractDao.get(contractId);
            if (contractSource == null) {
                throw new NotFoundException(String.format("Contract not found, contractId='%s'", contractId));
            }
            Long contractSourceId = contractSource.getId();
            contractSource.setId(null);
            contractSource.setEventId(eventId);
            contractSource.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            Contractstatus status = TypeUtil.toEnumField(statusChanged.getSetField().getFieldName(), Contractstatus.class);
            if (status == null) {
                throw new IllegalArgumentException("Illegal contract status: " + statusChanged);
            }
            contractSource.setStatus(status);
            if (statusChanged.isSetTerminated()) {
                contractSource.setStatusTerminatedAt(TypeUtil.stringToLocalDateTime(statusChanged.getTerminated().getTerminatedAt()));
            }
            contractDao.update(contractId);
            long cntrctId = contractDao.save(contractSource);

            List<ContractAdjustment> adjustments = contractAdjustmentDao.getByCntrctId(contractSourceId);
            adjustments.forEach(a -> {
                a.setId(null);
                a.setCntrctId(cntrctId);
            });
            contractAdjustmentDao.save(adjustments);

            List<PayoutTool> payoutTools = payoutToolDao.getByCntrctId(contractSourceId);
            payoutTools.forEach(pt -> {
                pt.setId(null);
                pt.setCntrctId(cntrctId);
            });
            payoutToolDao.save(payoutTools);

            log.info("Contract status has been saved, eventId={}, partyId={}, contractId={}", eventId, partyId, contractId);
        });
    }

    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
