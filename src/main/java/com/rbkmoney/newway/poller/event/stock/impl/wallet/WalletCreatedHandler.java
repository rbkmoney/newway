package com.rbkmoney.newway.poller.event.stock.impl.wallet;

import com.rbkmoney.fistful.wallet.Change;
import com.rbkmoney.fistful.wallet.TimestampedChange;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.domain.tables.pojos.Wallet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletCreatedHandler extends AbstractWalletHandler {

    private final WalletDao walletDao;

    @Getter
    private final Filter filter =
            new PathConditionFilter(new PathConditionRule("change.created", new IsNullCondition().not()));

    @Override
    public void handle(TimestampedChange timestampedChange, MachineEvent event) {
        Change change = timestampedChange.getChange();
        long sequenceId = event.getEventId();
        String walletId = event.getSourceId();
        log.info("Start wallet created handling, sequenceId={}, walletId={}", sequenceId, walletId);
        Wallet wallet = new Wallet();
        initDefaultFields(event, sequenceId, walletId, wallet, timestampedChange.getOccuredAt());

        wallet.setWalletName(change.getCreated().getName());
        wallet.setExternalId(change.getCreated().getExternalId());

        walletDao.save(wallet).ifPresentOrElse(
                dbContractId -> log
                        .info("Wallet created has been saved, sequenceId={}, walletId={}", sequenceId, walletId),
                () -> log.info("Wallet created bound duplicated, sequenceId={}, walletId={}", sequenceId, walletId));
    }

}
