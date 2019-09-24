package com.rbkmoney.newway.poller.event_stock.impl.withdrawal;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbkmoney.fistful.base.Cash;
import com.rbkmoney.fistful.withdrawal.Change;
import com.rbkmoney.fistful.withdrawal.SinkEvent;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.domain.enums.WithdrawalStatus;
import com.rbkmoney.newway.domain.tables.pojos.Withdrawal;
import com.rbkmoney.newway.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WithdrawalCreatedHandler extends AbstractWithdrawalHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalDao withdrawalDao;

    private final Filter filter;

    public WithdrawalCreatedHandler(WithdrawalDao withdrawalDao) {
        this.withdrawalDao = withdrawalDao;
        this.filter = new PathConditionFilter(new PathConditionRule("created", new IsNullCondition().not()));
    }

    @Override
    public void handle(Change change, SinkEvent event) {
        log.info("Start withdrawal created handling, eventId={}, walletId={}", event.getId(), event.getSource());
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setEventId(event.getId());
        withdrawal.setSequenceId(event.getPayload().getSequence());
        withdrawal.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        withdrawal.setEventOccuredAt(TypeUtil.stringToLocalDateTime(event.getPayload().getOccuredAt()));
        withdrawal.setWithdrawalId(event.getSource());
        withdrawal.setWalletId(change.getCreated().getSource());
        withdrawal.setDestinationId(change.getCreated().getDestination());
        withdrawal.setExternalId(change.getCreated().getExternalId());
        if (change.getCreated().isSetStatus()) {
            withdrawal.setWithdrawalStatus(TBaseUtil.unionFieldToEnum(change.getStatusChanged(), WithdrawalStatus.class));
        }
        if (change.getCreated().isSetContext()) {
            Map<String, JsonNode> jsonNodeMap = change.getCreated().getContext().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> JsonUtil.tBaseToJsonNode(e.getValue())));
            withdrawal.setContextJson(JsonUtil.objectToJsonString(jsonNodeMap));
        }
        Cash cash = change.getCreated().getBody();
        withdrawal.setAmount(cash.getAmount());
        withdrawal.setCurrencyCode(cash.getCurrency().getSymbolicCode());
        withdrawal.setWithdrawalStatus(WithdrawalStatus.pending);

        withdrawalDao.updateNotCurrent(event.getSource());
        withdrawalDao.save(withdrawal);
        log.info("Withdrawal have been saved, eventId={}, walletId={}", event.getId(), event.getSource());
    }

    @Override
    public Filter<Change> getFilter() {
        return filter;
    }
}
