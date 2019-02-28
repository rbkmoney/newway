package com.rbkmoney.newway.poller.event_stock.impl.deposit;

import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.deposit.Change;
import com.rbkmoney.fistful.deposit.SinkEvent;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.domain.enums.DepositStatus;
import com.rbkmoney.newway.domain.tables.pojos.Deposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DepositCreatedHandler extends AbstractDepositHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DepositDao depositDao;

    private final Filter filter;

    public DepositCreatedHandler(DepositDao depositDao) {
        this.depositDao = depositDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start deposit created handling, eventId={}, depositId={}", event.getId(), event.getSource());
        Deposit deposit = new Deposit();
        deposit.setEventId(event.getId());
        deposit.setSequenceId(event.getPayload().getSequence());
        deposit.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        deposit.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        deposit.setDepositId(event.getSource());
        deposit.setSourceId(change.getCreated().getSource());
        deposit.setWalletId(change.getCreated().getWallet());

        Cash cash = change.getCreated().getBody();
        deposit.setAmount(cash.getAmount());
        deposit.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        deposit.setDepositStatus(DepositStatus.pending);
        deposit.setExternalId(change.getCreated().getExternalId());

        depositDao.save(deposit);
        log.info("Deposit have been saved, eventId={}, depositId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
