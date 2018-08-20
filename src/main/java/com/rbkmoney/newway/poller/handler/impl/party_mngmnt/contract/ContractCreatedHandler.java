package com.rbkmoney.newway.poller.handler.impl.party_mngmnt.contract;

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
import com.rbkmoney.newway.poller.handler.impl.party_mngmnt.AbstractPartyManagementHandler;
import com.rbkmoney.newway.util.ContractUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContractCreatedHandler extends AbstractPartyManagementHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ContractDao contractDao;
    private final ContractAdjustmentDao contractAdjustmentDao;
    private final PayoutToolDao payoutToolDao;

    private final Filter filter;

    public ContractCreatedHandler(ContractDao contractDao, ContractAdjustmentDao contractAdjustmentDao, PayoutToolDao payoutToolDao) {
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
                .filter(e -> e.isSetContractEffect() && e.getContractEffect().getEffect().isSetCreated()).forEach(e -> {
            ContractEffectUnit contractEffectUnit = e.getContractEffect();
            com.rbkmoney.damsel.domain.Contract contractCreated = contractEffectUnit.getEffect().getCreated();
            String contractId = contractEffectUnit.getContractId();
            String partyId = event.getSource().getPartyId();
            log.info("Start contract created handling, eventId={}, partyId={}, contractId={}", eventId, contractId);
            Contract contract = new Contract();
            contract.setEventId(eventId);
            contract.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
            contract.setContractId(contractId);
            contract.setPartyId(partyId);
            if (contractCreated.isSetPaymentInstitution()) {
                contract.setPaymentInstitutionId(contractCreated.getPaymentInstitution().getId());
            }
            contract.setCreatedAt(TypeUtil.stringToLocalDateTime(contractCreated.getCreatedAt()));
            if (contractCreated.isSetValidSince()) {
                contract.setValidSince(TypeUtil.stringToLocalDateTime(contractCreated.getValidSince()));
            }
            if (contractCreated.isSetValidUntil()) {
                contract.setValidUntil(TypeUtil.stringToLocalDateTime(contractCreated.getValidUntil()));
            }
            Contractstatus status = TypeUtil.toEnumField(contractCreated.getStatus().getSetField().getFieldName(), Contractstatus.class);
            if (status == null) {
                throw new IllegalArgumentException("Illegal contract status: "+contractCreated.getStatus());
            }
            contract.setStatus(status);
            if (contractCreated.getStatus().isSetTerminated()) {
                contract.setStatusTerminatedAt(TypeUtil.stringToLocalDateTime(contractCreated.getStatus().getTerminated().getTerminatedAt()));
            }
            contract.setTermsId(contractCreated.getTerms().getId());
            if (contractCreated.isSetLegalAgreement()) {
                ContractUtil.fillContractLegalAgreementFields(contract, contractCreated.getLegalAgreement());
            }
            if (contractCreated.isSetReportPreferences() && contractCreated.getReportPreferences().isSetServiceAcceptanceActPreferences()) {
                ContractUtil.fillReportPreferences(contract, contractCreated.getReportPreferences().getServiceAcceptanceActPreferences());
            }
            contract.setContractorId(contractCreated.getContractorId());
            long cntrctId = contractDao.save(contract);

            List<com.rbkmoney.newway.domain.tables.pojos.ContractAdjustment> adjustments = ContractUtil.convertContractAdjustments(contractCreated, cntrctId);
            contractAdjustmentDao.save(adjustments);

            List<com.rbkmoney.newway.domain.tables.pojos.PayoutTool> payoutTools = ContractUtil.convertPayoutTools(contractCreated, cntrctId);
            payoutToolDao.save(payoutTools);

            log.info("Contract has been saved, eventId={}, contractId={}", eventId, contractId);
        });
    }



    @Override
    public Filter<PartyChange> getFilter() {
        return filter;
    }
}
