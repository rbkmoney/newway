package com.rbkmoney.newway.dao.function;

import com.rbkmoney.newway.AbstractIntegrationTest;
import com.rbkmoney.newway.dao.invoicing.iface.CashFlowDao;
import com.rbkmoney.newway.domain.enums.AdjustmentCashFlowType;
import com.rbkmoney.newway.domain.enums.CashFlowAccount;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.tables.pojos.CashFlow;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@Transactional
public class CashFlowAggregateFunctionTest extends AbstractIntegrationTest {

    @Autowired
    private CashFlowDao cashFlowDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testWithDifferentCashFlows() {
        List<CashFlow> cashFlows = new ArrayList<>();

        CashFlow cashFlowPaymentAmount = new CashFlow();
        cashFlowPaymentAmount.setObjId(1L);
        cashFlowPaymentAmount.setAmount(1000L);
        cashFlowPaymentAmount.setCurrencyCode("RUB");
        cashFlowPaymentAmount.setSourceAccountId(1L);
        cashFlowPaymentAmount.setSourceAccountType(CashFlowAccount.provider);
        cashFlowPaymentAmount.setSourceAccountTypeValue("settlement");
        cashFlowPaymentAmount.setDestinationAccountId(2L);
        cashFlowPaymentAmount.setDestinationAccountType(CashFlowAccount.merchant);
        cashFlowPaymentAmount.setDestinationAccountTypeValue("settlement");
        cashFlowPaymentAmount.setObjType(PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentAmount);

        CashFlow cashFlowPaymentFee = new CashFlow();
        cashFlowPaymentFee.setObjId(1L);
        cashFlowPaymentFee.setAmount(10L);
        cashFlowPaymentFee.setCurrencyCode("RUB");
        cashFlowPaymentFee.setSourceAccountId(2L);
        cashFlowPaymentFee.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowPaymentFee.setSourceAccountTypeValue("settlement");
        cashFlowPaymentFee.setDestinationAccountId(2L);
        cashFlowPaymentFee.setDestinationAccountType(CashFlowAccount.system);
        cashFlowPaymentFee.setDestinationAccountTypeValue("settlement");
        cashFlowPaymentFee.setObjType(PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentFee);

        CashFlow cashFlowPaymentExternalIncomeFee = new CashFlow();
        cashFlowPaymentExternalIncomeFee.setObjId(1L);
        cashFlowPaymentExternalIncomeFee.setAmount(3L);
        cashFlowPaymentExternalIncomeFee.setCurrencyCode("RUB");
        cashFlowPaymentExternalIncomeFee.setSourceAccountId(2L);
        cashFlowPaymentExternalIncomeFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowPaymentExternalIncomeFee.setSourceAccountTypeValue("settlement");
        cashFlowPaymentExternalIncomeFee.setDestinationAccountId(3L);
        cashFlowPaymentExternalIncomeFee.setDestinationAccountType(CashFlowAccount.external);
        cashFlowPaymentExternalIncomeFee.setDestinationAccountTypeValue("income");
        cashFlowPaymentExternalIncomeFee.setObjType(PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentExternalIncomeFee);

        CashFlow cashFlowPaymentExternalOutcomeFee = new CashFlow();
        cashFlowPaymentExternalOutcomeFee.setObjId(1L);
        cashFlowPaymentExternalOutcomeFee.setAmount(3L);
        cashFlowPaymentExternalOutcomeFee.setCurrencyCode("RUB");
        cashFlowPaymentExternalOutcomeFee.setSourceAccountId(2L);
        cashFlowPaymentExternalOutcomeFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowPaymentExternalOutcomeFee.setSourceAccountTypeValue("settlement");
        cashFlowPaymentExternalOutcomeFee.setDestinationAccountId(4L);
        cashFlowPaymentExternalOutcomeFee.setDestinationAccountType(CashFlowAccount.external);
        cashFlowPaymentExternalOutcomeFee.setDestinationAccountTypeValue("outcome");
        cashFlowPaymentExternalOutcomeFee.setObjType(PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentExternalOutcomeFee);

        CashFlow cashFlowPaymentProviderFee = new CashFlow();
        cashFlowPaymentProviderFee.setObjId(1L);
        cashFlowPaymentProviderFee.setAmount(3L);
        cashFlowPaymentProviderFee.setCurrencyCode("RUB");
        cashFlowPaymentProviderFee.setSourceAccountId(2L);
        cashFlowPaymentProviderFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowPaymentProviderFee.setSourceAccountTypeValue("settlement");
        cashFlowPaymentProviderFee.setDestinationAccountId(5L);
        cashFlowPaymentProviderFee.setDestinationAccountType(CashFlowAccount.provider);
        cashFlowPaymentProviderFee.setDestinationAccountTypeValue("settlement");
        cashFlowPaymentProviderFee.setObjType(PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentProviderFee);

        CashFlow cashFlowPaymentGuaranteeDeposit = new CashFlow();
        cashFlowPaymentGuaranteeDeposit.setObjId(1L);
        cashFlowPaymentGuaranteeDeposit.setAmount(30L);
        cashFlowPaymentGuaranteeDeposit.setCurrencyCode("RUB");
        cashFlowPaymentGuaranteeDeposit.setSourceAccountId(2L);
        cashFlowPaymentGuaranteeDeposit.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowPaymentGuaranteeDeposit.setSourceAccountTypeValue("settlement");
        cashFlowPaymentGuaranteeDeposit.setDestinationAccountId(5L);
        cashFlowPaymentGuaranteeDeposit.setDestinationAccountType(CashFlowAccount.merchant);
        cashFlowPaymentGuaranteeDeposit.setDestinationAccountTypeValue("guarantee");
        cashFlowPaymentGuaranteeDeposit.setObjType(PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentGuaranteeDeposit);

        CashFlow cashFlowRefundAmount = new CashFlow();
        cashFlowRefundAmount.setObjId(1L);
        cashFlowRefundAmount.setAmount(1000L);
        cashFlowRefundAmount.setCurrencyCode("RUB");
        cashFlowRefundAmount.setSourceAccountId(2L);
        cashFlowRefundAmount.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowRefundAmount.setSourceAccountTypeValue("settlement");
        cashFlowRefundAmount.setDestinationAccountId(5L);
        cashFlowRefundAmount.setDestinationAccountType(CashFlowAccount.provider);
        cashFlowRefundAmount.setDestinationAccountTypeValue("settlement");
        cashFlowRefundAmount.setObjType(PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundAmount);

        CashFlow cashFlowRefundFee = new CashFlow();
        cashFlowRefundFee.setObjId(1L);
        cashFlowRefundFee.setAmount(10L);
        cashFlowRefundFee.setCurrencyCode("RUB");
        cashFlowRefundFee.setSourceAccountId(2L);
        cashFlowRefundFee.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowRefundFee.setSourceAccountTypeValue("settlement");
        cashFlowRefundFee.setDestinationAccountId(2L);
        cashFlowRefundFee.setDestinationAccountType(CashFlowAccount.system);
        cashFlowRefundFee.setDestinationAccountTypeValue("settlement");
        cashFlowRefundFee.setObjType(PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundFee);

        CashFlow cashFlowRefundExternalIncomeFee = new CashFlow();
        cashFlowRefundExternalIncomeFee.setObjId(1L);
        cashFlowRefundExternalIncomeFee.setAmount(3L);
        cashFlowRefundExternalIncomeFee.setCurrencyCode("RUB");
        cashFlowRefundExternalIncomeFee.setSourceAccountId(2L);
        cashFlowRefundExternalIncomeFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowRefundExternalIncomeFee.setSourceAccountTypeValue("settlement");
        cashFlowRefundExternalIncomeFee.setDestinationAccountId(3L);
        cashFlowRefundExternalIncomeFee.setDestinationAccountType(CashFlowAccount.external);
        cashFlowRefundExternalIncomeFee.setDestinationAccountTypeValue("income");
        cashFlowRefundExternalIncomeFee.setObjType(PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundExternalIncomeFee);

        CashFlow cashFlowRefundExternalOutcomeFee = new CashFlow();
        cashFlowRefundExternalOutcomeFee.setObjId(1L);
        cashFlowRefundExternalOutcomeFee.setAmount(3L);
        cashFlowRefundExternalOutcomeFee.setCurrencyCode("RUB");
        cashFlowRefundExternalOutcomeFee.setSourceAccountId(2L);
        cashFlowRefundExternalOutcomeFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowRefundExternalOutcomeFee.setSourceAccountTypeValue("settlement");
        cashFlowRefundExternalOutcomeFee.setDestinationAccountId(4L);
        cashFlowRefundExternalOutcomeFee.setDestinationAccountType(CashFlowAccount.external);
        cashFlowRefundExternalOutcomeFee.setDestinationAccountTypeValue("outcome");
        cashFlowRefundExternalOutcomeFee.setObjType(PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundExternalOutcomeFee);

        CashFlow cashFlowRefundProviderFee = new CashFlow();
        cashFlowRefundProviderFee.setObjId(1L);
        cashFlowRefundProviderFee.setAmount(3L);
        cashFlowRefundProviderFee.setCurrencyCode("RUB");
        cashFlowRefundProviderFee.setSourceAccountId(2L);
        cashFlowRefundProviderFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowRefundProviderFee.setSourceAccountTypeValue("settlement");
        cashFlowRefundProviderFee.setDestinationAccountId(5L);
        cashFlowRefundProviderFee.setDestinationAccountType(CashFlowAccount.provider);
        cashFlowRefundProviderFee.setDestinationAccountTypeValue("settlement");
        cashFlowRefundProviderFee.setObjType(PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundProviderFee);

        CashFlow cashFlowPayoutAmount = new CashFlow();
        cashFlowPayoutAmount.setObjId(1L);
        cashFlowPayoutAmount.setAmount(1000L);
        cashFlowPayoutAmount.setCurrencyCode("RUB");
        cashFlowPayoutAmount.setSourceAccountId(2L);
        cashFlowPayoutAmount.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowPayoutAmount.setSourceAccountTypeValue("settlement");
        cashFlowPayoutAmount.setDestinationAccountId(7L);
        cashFlowPayoutAmount.setDestinationAccountType(CashFlowAccount.merchant);
        cashFlowPayoutAmount.setDestinationAccountTypeValue("payout");
        cashFlowPayoutAmount.setObjType(PaymentChangeType.payout);
        cashFlows.add(cashFlowPayoutAmount);

        CashFlow cashFlowPayoutFee = new CashFlow();
        cashFlowPayoutFee.setObjId(1L);
        cashFlowPayoutFee.setAmount(10L);
        cashFlowPayoutFee.setCurrencyCode("RUB");
        cashFlowPayoutFee.setSourceAccountId(1L);
        cashFlowPayoutFee.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowPayoutFee.setSourceAccountTypeValue("settlement");
        cashFlowPayoutFee.setDestinationAccountId(2L);
        cashFlowPayoutFee.setDestinationAccountType(CashFlowAccount.system);
        cashFlowPayoutFee.setDestinationAccountTypeValue("settlement");
        cashFlowPayoutFee.setObjType(PaymentChangeType.payout);
        cashFlows.add(cashFlowPayoutFee);

        CashFlow cashFlowPayoutFixedFee = new CashFlow();
        cashFlowPayoutFixedFee.setObjId(1L);
        cashFlowPayoutFixedFee.setAmount(100L);
        cashFlowPayoutFixedFee.setCurrencyCode("RUB");
        cashFlowPayoutFixedFee.setSourceAccountId(7L);
        cashFlowPayoutFixedFee.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowPayoutFixedFee.setSourceAccountTypeValue("payout");
        cashFlowPayoutFixedFee.setDestinationAccountId(2L);
        cashFlowPayoutFixedFee.setDestinationAccountType(CashFlowAccount.system);
        cashFlowPayoutFixedFee.setDestinationAccountTypeValue("settlement");
        cashFlowPayoutFixedFee.setObjType(PaymentChangeType.payout);
        cashFlows.add(cashFlowPayoutFixedFee);

        CashFlow cashFlowAdjustmentAmount = new CashFlow();
        cashFlowAdjustmentAmount.setObjId(1L);
        cashFlowAdjustmentAmount.setAmount(1000L);
        cashFlowAdjustmentAmount.setCurrencyCode("RUB");
        cashFlowAdjustmentAmount.setSourceAccountId(1L);
        cashFlowAdjustmentAmount.setSourceAccountType(CashFlowAccount.provider);
        cashFlowAdjustmentAmount.setSourceAccountTypeValue("settlement");
        cashFlowAdjustmentAmount.setDestinationAccountId(2L);
        cashFlowAdjustmentAmount.setDestinationAccountType(CashFlowAccount.merchant);
        cashFlowAdjustmentAmount.setDestinationAccountTypeValue("settlement");
        cashFlowAdjustmentAmount.setObjType(PaymentChangeType.adjustment);
        cashFlowAdjustmentAmount.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentAmount);

        CashFlow cashFlowAdjustmentFee = new CashFlow();
        cashFlowAdjustmentFee.setObjId(1L);
        cashFlowAdjustmentFee.setAmount(10L);
        cashFlowAdjustmentFee.setCurrencyCode("RUB");
        cashFlowAdjustmentFee.setSourceAccountId(2L);
        cashFlowAdjustmentFee.setSourceAccountType(CashFlowAccount.merchant);
        cashFlowAdjustmentFee.setSourceAccountTypeValue("settlement");
        cashFlowAdjustmentFee.setDestinationAccountId(2L);
        cashFlowAdjustmentFee.setDestinationAccountType(CashFlowAccount.system);
        cashFlowAdjustmentFee.setDestinationAccountTypeValue("settlement");
        cashFlowAdjustmentFee.setObjType(PaymentChangeType.adjustment);
        cashFlowAdjustmentFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentFee);

        CashFlow cashFlowAdjustmentExternalIncomeFee = new CashFlow();
        cashFlowAdjustmentExternalIncomeFee.setObjId(1L);
        cashFlowAdjustmentExternalIncomeFee.setAmount(3L);
        cashFlowAdjustmentExternalIncomeFee.setCurrencyCode("RUB");
        cashFlowAdjustmentExternalIncomeFee.setSourceAccountId(2L);
        cashFlowAdjustmentExternalIncomeFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowAdjustmentExternalIncomeFee.setSourceAccountTypeValue("settlement");
        cashFlowAdjustmentExternalIncomeFee.setDestinationAccountId(3L);
        cashFlowAdjustmentExternalIncomeFee.setDestinationAccountType(CashFlowAccount.external);
        cashFlowAdjustmentExternalIncomeFee.setDestinationAccountTypeValue("income");
        cashFlowAdjustmentExternalIncomeFee.setObjType(PaymentChangeType.adjustment);
        cashFlowAdjustmentExternalIncomeFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentExternalIncomeFee);

        CashFlow cashFlowAdjustmentExternalOutcomeFee = new CashFlow();
        cashFlowAdjustmentExternalOutcomeFee.setObjId(1L);
        cashFlowAdjustmentExternalOutcomeFee.setAmount(3L);
        cashFlowAdjustmentExternalOutcomeFee.setCurrencyCode("RUB");
        cashFlowAdjustmentExternalOutcomeFee.setSourceAccountId(2L);
        cashFlowAdjustmentExternalOutcomeFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowAdjustmentExternalOutcomeFee.setSourceAccountTypeValue("settlement");
        cashFlowAdjustmentExternalOutcomeFee.setDestinationAccountId(4L);
        cashFlowAdjustmentExternalOutcomeFee.setDestinationAccountType(CashFlowAccount.external);
        cashFlowAdjustmentExternalOutcomeFee.setDestinationAccountTypeValue("outcome");
        cashFlowAdjustmentExternalOutcomeFee.setObjType(PaymentChangeType.adjustment);
        cashFlowAdjustmentExternalOutcomeFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentExternalOutcomeFee);

        CashFlow cashFlowAdjustmentProviderFee = new CashFlow();
        cashFlowAdjustmentProviderFee.setObjId(1L);
        cashFlowAdjustmentProviderFee.setAmount(3L);
        cashFlowAdjustmentProviderFee.setCurrencyCode("RUB");
        cashFlowAdjustmentProviderFee.setSourceAccountId(2L);
        cashFlowAdjustmentProviderFee.setSourceAccountType(CashFlowAccount.system);
        cashFlowAdjustmentProviderFee.setSourceAccountTypeValue("settlement");
        cashFlowAdjustmentProviderFee.setDestinationAccountId(5L);
        cashFlowAdjustmentProviderFee.setDestinationAccountType(CashFlowAccount.provider);
        cashFlowAdjustmentProviderFee.setDestinationAccountTypeValue("settlement");
        cashFlowAdjustmentProviderFee.setObjType(PaymentChangeType.adjustment);
        cashFlowAdjustmentProviderFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentProviderFee);

        cashFlowDao.save(cashFlows);

        assertEquals(cashFlowPaymentAmount.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentExternalIncomeFee.getAmount() + cashFlowPaymentExternalOutcomeFee.getAmount(), (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_external_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentProviderFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_provider_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentGuaranteeDeposit.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_guarantee_deposit(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(cashFlowRefundAmount.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowRefundFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowRefundExternalIncomeFee.getAmount() + cashFlowRefundExternalOutcomeFee.getAmount(), (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_external_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowRefundProviderFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_provider_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(cashFlowPayoutAmount.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payout_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPayoutFixedFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payout_fixed_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPayoutFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_payout_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(cashFlowAdjustmentAmount.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowAdjustmentFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowAdjustmentExternalIncomeFee.getAmount() + cashFlowAdjustmentExternalOutcomeFee.getAmount(), (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_external_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowAdjustmentProviderFee.getAmount(), jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_provider_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
    }

    @Test
    public void testWhenDataNotFound() {
        CashFlow notCashFlow = random(CashFlow.class, "objId");
        notCashFlow.setObjId(1L);
        cashFlowDao.save(Collections.singletonList(notCashFlow));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_external_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_provider_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payment_guarantee_deposit(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_external_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_refund_provider_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payout_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payout_fixed_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_payout_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_amount(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_external_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT SUM(nw.get_adjustment_provider_fee(nw.cash_flow.*)) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
    }

}
