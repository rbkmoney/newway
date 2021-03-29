package com.rbkmoney.newway.handler.event.stock.impl.deposit.revert;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.deposit.revert.iface.DepositRevertDao;
import com.rbkmoney.newway.domain.enums.DepositRevertStatus;
import com.rbkmoney.newway.domain.tables.pojos.DepositRevert;
import com.rbkmoney.newway.factory.MachineEventCopyFactory;
import com.rbkmoney.newway.handler.event.stock.impl.deposit.DepositHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositRevertCreatedHandler implements DepositHandler {

    private final DepositRevertDao depositRevertDao;
    private final MachineEventCopyFactory<DepositRevert, String> depositRevertMachineEventCopyFactory;

    @Getter
    private final Filter filter = new PathConditionFilter(
            new PathConditionRule("change.revert.payload.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String depositId = event.getSourceId();
        log.info("Start deposit revert created handling, sequenceId={}, depositId={}", sequenceId, depositId);

        DepositRevert depositRevert = depositRevertMachineEventCopyFactory
                        .create(event, sequenceId, depositId, timestampedChange.getOccuredAt());

        var revert = change.getRevert().getPayload().getCreated().getRevert();
        depositRevert.setRevertId(revert.getId());
        depositRevert.setSourceId(revert.getSourceId());
        depositRevert.setWalletId(revert.getWalletId());
        depositRevert.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));

        Cash cash = revert.getBody();
        depositRevert.setAmount(cash.getAmount());
        depositRevert.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        depositRevert.setStatus(DepositRevertStatus.pending);
        depositRevert.setExternalId(revert.getExternalId());
        depositRevert.setReason(revert.getReason());
        depositRevert.setPartyRevision(revert.getPartyRevision());
        depositRevert.setDomainRevision(revert.getDomainRevision());

        depositRevertDao.save(depositRevert).ifPresentOrElse(
                dbContractId -> log.info("Deposit revert created has been saved, sequenceId={}, depositId={}",
                        sequenceId, depositId),
                () -> log.info("Deposit revert created bound duplicated, sequenceId={}, depositId={}",
                        sequenceId, depositId));
    }
}
