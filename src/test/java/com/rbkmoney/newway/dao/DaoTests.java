package com.rbkmoney.newway.dao;

import com.rbkmoney.newway.dao.deposit.iface.DepositDao;
import com.rbkmoney.newway.dao.destination.iface.DestinationDao;
import com.rbkmoney.newway.dao.dominant.iface.DominantDao;
import com.rbkmoney.newway.dao.dominant.impl.*;
import com.rbkmoney.newway.dao.identity.iface.ChallengeDao;
import com.rbkmoney.newway.dao.identity.iface.IdentityDao;
import com.rbkmoney.newway.dao.invoicing.iface.*;
import com.rbkmoney.newway.dao.invoicing.impl.ContractIdsGeneratorDaoImpl;
import com.rbkmoney.newway.dao.invoicing.impl.PaymentIdsGeneratorDaoImpl;
import com.rbkmoney.newway.dao.party.iface.*;
import com.rbkmoney.newway.dao.payout.iface.PayoutDao;
import com.rbkmoney.newway.dao.payout.iface.PayoutSummaryDao;
import com.rbkmoney.newway.dao.rate.iface.RateDao;
import com.rbkmoney.newway.dao.recurrent_payment_tool.iface.RecurrentPaymentToolDao;
import com.rbkmoney.newway.dao.source.iface.SourceDao;
import com.rbkmoney.newway.dao.wallet.iface.WalletDao;
import com.rbkmoney.newway.dao.withdrawal.iface.WithdrawalDao;
import com.rbkmoney.newway.dao.withdrawal_session.iface.WithdrawalSessionDao;
import com.rbkmoney.newway.domain.enums.AdjustmentCashFlowType;
import com.rbkmoney.newway.domain.enums.CashFlowAccount;
import com.rbkmoney.newway.domain.enums.PaymentChangeType;
import com.rbkmoney.newway.domain.enums.PaymentStatus;
import com.rbkmoney.newway.domain.tables.pojos.Calendar;
import com.rbkmoney.newway.domain.tables.pojos.Currency;
import com.rbkmoney.newway.domain.tables.pojos.*;
import com.rbkmoney.newway.model.InvoicingKey;
import com.rbkmoney.newway.model.InvoicingType;
import com.rbkmoney.newway.service.CashFlowService;
import com.rbkmoney.newway.util.HashUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.util.*;
import java.util.stream.LongStream;

import static com.rbkmoney.newway.dao.DaoUtils.createCashFlow;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

public class DaoTests extends AbstractAppDaoTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DepositDao depositDao;
    @Autowired
    private DestinationDao destinationDao;
    @Autowired
    private CalendarDaoImpl calendarDao;
    @Autowired
    private CategoryDaoImpl categoryDao;
    @Autowired
    private CurrencyDaoImpl currencyDao;
    @Autowired
    private InspectorDaoImpl inspectorDao;
    @Autowired
    private PaymentInstitutionDaoImpl paymentInstitutionDao;
    @Autowired
    private PaymentMethodDaoImpl paymentMethodDao;
    @Autowired
    private PayoutMethodDaoImpl payoutMethodDao;
    @Autowired
    private ProviderDaoImpl providerDao;
    @Autowired
    private WithdrawalProviderDaoImpl withdrawalProviderDao;
    @Autowired
    private ProxyDaoImpl proxyDao;
    @Autowired
    private TerminalDaoImpl terminalDao;
    @Autowired
    private TermSetHierarchyDaoImpl termSetHierarchyDao;
    @Autowired
    private DominantDao dominantDao;
    @Autowired
    private CashFlowDao cashFlowDao;
    @Autowired
    private ChallengeDao challengeDao;
    @Autowired
    private IdentityDao identityDao;
    @Autowired
    private AdjustmentDao adjustmentDao;
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private InvoiceCartDao invoiceCartDao;
    @Autowired
    private InvoiceDao invoiceDao;
    @Autowired
    private RefundDao refundDao;
    @Autowired
    private ContractAdjustmentDao contractAdjustmentDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ContractorDao contractorDao;
    @Autowired
    private PartyDao partyDao;
    @Autowired
    private PayoutToolDao payoutToolDao;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private PayoutDao payoutDao;
    @Autowired
    private PayoutSummaryDao payoutSummaryDao;
    @Autowired
    private RateDao rateDao;
    @Autowired
    private SourceDao sourceDao;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    private WithdrawalDao withdrawalDao;
    @Autowired
    private WithdrawalSessionDao withdrawalSessionDao;
    @Autowired
    private CashFlowService cashFlowService;
    @Autowired
    private PaymentIdsGeneratorDaoImpl idsGeneratorDao;
    @Autowired
    private RecurrentPaymentToolDao recurrentPaymentToolDao;
    @Autowired
    private ContractIdsGeneratorDaoImpl contractIdsGeneratorDao;

    @Test
    public void depositDaoTest() {
        jdbcTemplate.execute("truncate table nw.deposit cascade");
        Deposit deposit = random(Deposit.class);
        deposit.setCurrent(true);
        Long id = depositDao.save(deposit);
        deposit.setId(id);
        assertEquals(deposit, depositDao.get(deposit.getDepositId()));
        depositDao.updateNotCurrent(deposit.getDepositId());
        assertNull(depositDao.get(deposit.getDepositId()));
    }

    @Test
    public void destinationDaoTest() {
        jdbcTemplate.execute("truncate table nw.destination cascade");
        Destination destination = random(Destination.class);
        destination.setCurrent(true);
        Long id = destinationDao.save(destination);
        destination.setId(id);
        assertEquals(destination, destinationDao.get(destination.getDestinationId()));
        destinationDao.updateNotCurrent(destination.getDestinationId());
        Assert.assertNull(destinationDao.get(destination.getDestinationId()));
    }

    @Test
    public void dominantDaoTest() {
        jdbcTemplate.execute("truncate table nw.calendar cascade");
        jdbcTemplate.execute("truncate table nw.category cascade");
        jdbcTemplate.execute("truncate table nw.currency cascade");
        jdbcTemplate.execute("truncate table nw.inspector cascade");
        jdbcTemplate.execute("truncate table nw.payment_institution cascade");
        jdbcTemplate.execute("truncate table nw.payment_method cascade");
        jdbcTemplate.execute("truncate table nw.payout_method cascade");
        jdbcTemplate.execute("truncate table nw.provider cascade");
        jdbcTemplate.execute("truncate table nw.withdrawal_provider cascade");
        jdbcTemplate.execute("truncate table nw.proxy cascade");
        jdbcTemplate.execute("truncate table nw.terminal cascade");
        jdbcTemplate.execute("truncate table nw.term_set_hierarchy cascade");

        Calendar calendar = random(Calendar.class);
        calendar.setCurrent(true);
        calendarDao.save(calendar);
        calendarDao.updateNotCurrent(calendar.getCalendarRefId());

        Category category = random(Category.class);
        category.setCurrent(true);
        categoryDao.save(category);
        categoryDao.updateNotCurrent(category.getCategoryRefId());

        Currency currency = random(Currency.class);
        currency.setCurrent(true);
        currencyDao.save(currency);
        currencyDao.updateNotCurrent(currency.getCurrencyRefId());

        Inspector inspector = random(Inspector.class);
        inspector.setCurrent(true);
        inspectorDao.save(inspector);
        inspectorDao.updateNotCurrent(inspector.getInspectorRefId());

        PaymentInstitution paymentInstitution = random(PaymentInstitution.class);
        paymentInstitution.setCurrent(true);
        paymentInstitutionDao.save(paymentInstitution);
        paymentInstitutionDao.updateNotCurrent(paymentInstitution.getPaymentInstitutionRefId());

        PaymentMethod paymentMethod = random(PaymentMethod.class);
        paymentMethod.setCurrent(true);
        paymentMethodDao.save(paymentMethod);
        paymentMethodDao.updateNotCurrent(paymentMethod.getPaymentMethodRefId());

        PayoutMethod payoutMethod = random(PayoutMethod.class);
        payoutMethod.setCurrent(true);
        payoutMethodDao.save(payoutMethod);
        payoutMethodDao.updateNotCurrent(payoutMethod.getPayoutMethodRefId());

        Provider provider = random(Provider.class);
        provider.setCurrent(true);
        providerDao.save(provider);
        providerDao.updateNotCurrent(provider.getProviderRefId());

        WithdrawalProvider withdrawalProvider = random(WithdrawalProvider.class);
        withdrawalProvider.setCurrent(true);
        withdrawalProviderDao.save(withdrawalProvider);
        withdrawalProviderDao.updateNotCurrent(withdrawalProvider.getWithdrawalProviderRefId());

        Proxy proxy = random(Proxy.class);
        proxy.setCurrent(true);
        proxyDao.save(proxy);
        proxyDao.updateNotCurrent(proxy.getProxyRefId());

        Terminal terminal = random(Terminal.class);
        terminal.setCurrent(true);
        terminalDao.save(terminal);
        terminalDao.updateNotCurrent(terminal.getTerminalRefId());

        TermSetHierarchy termSetHierarchy = random(TermSetHierarchy.class);
        termSetHierarchy.setCurrent(true);
        termSetHierarchyDao.save(termSetHierarchy);
        termSetHierarchyDao.updateNotCurrent(termSetHierarchy.getTermSetHierarchyRefId());

        Long lastVersionId = dominantDao.getLastVersionId();

        OptionalLong maxVersionId = LongStream.of(
                calendar.getVersionId(),
                category.getVersionId(),
                currency.getVersionId(),
                inspector.getVersionId(),
                paymentInstitution.getVersionId(),
                paymentMethod.getVersionId(),
                payoutMethod.getVersionId(),
                provider.getVersionId(),
                proxy.getVersionId(),
                terminal.getVersionId(),
                termSetHierarchy.getVersionId()).max();

        assertEquals(maxVersionId.getAsLong(), lastVersionId.longValue());
    }

    @Test
    public void differentCashFlowAggregateFunctionTest() {
        jdbcTemplate.execute("truncate table nw.cash_flow cascade");
        List<CashFlow> cashFlows = new ArrayList<>();

        CashFlow cashFlowPaymentAmount = createCashFlow(1L, 1000L, "RUB", 1L, CashFlowAccount.provider, "settlement", 2L, CashFlowAccount.merchant, "settlement", PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentAmount);
        CashFlow cashFlowPaymentFee = createCashFlow(1L, 10L, "RUB", 2L, CashFlowAccount.merchant, "settlement", 2L, CashFlowAccount.system, "settlement", PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentFee);
        CashFlow cashFlowPaymentExternalIncomeFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 3L, CashFlowAccount.external, "income", PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentExternalIncomeFee);
        CashFlow cashFlowPaymentExternalOutcomeFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 4L, CashFlowAccount.external, "outcome", PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentExternalOutcomeFee);
        CashFlow cashFlowPaymentProviderFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 5L, CashFlowAccount.provider, "settlement", PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentProviderFee);
        CashFlow cashFlowPaymentGuaranteeDeposit = createCashFlow(1L, 30L, "RUB", 2L, CashFlowAccount.merchant, "settlement", 5L, CashFlowAccount.merchant, "guarantee", PaymentChangeType.payment);
        cashFlows.add(cashFlowPaymentGuaranteeDeposit);
        CashFlow cashFlowRefundAmount = createCashFlow(1L, 1000L, "RUB", 2L, CashFlowAccount.merchant, "settlement", 5L, CashFlowAccount.provider, "settlement", PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundAmount);
        CashFlow cashFlowRefundFee = createCashFlow(1L, 10L, "RUB", 2L, CashFlowAccount.merchant, "settlement", 2L, CashFlowAccount.system, "settlement", PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundFee);
        CashFlow cashFlowRefundExternalIncomeFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 3L, CashFlowAccount.external, "income", PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundExternalIncomeFee);
        CashFlow cashFlowRefundExternalOutcomeFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 4L, CashFlowAccount.external, "outcome", PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundExternalOutcomeFee);
        CashFlow cashFlowRefundProviderFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 5L, CashFlowAccount.provider, "settlement", PaymentChangeType.refund);
        cashFlows.add(cashFlowRefundProviderFee);
        CashFlow cashFlowPayoutAmount = createCashFlow(1L, 1000L, "RUB", 2L, CashFlowAccount.merchant, "settlement", 7L, CashFlowAccount.merchant, "payout", PaymentChangeType.payout);
        cashFlows.add(cashFlowPayoutAmount);
        CashFlow cashFlowPayoutFee = createCashFlow(1L, 10L, "RUB", 1L, CashFlowAccount.merchant, "settlement", 2L, CashFlowAccount.system, "settlement", PaymentChangeType.payout);
        cashFlows.add(cashFlowPayoutFee);
        CashFlow cashFlowPayoutFixedFee = createCashFlow(1L, 100L, "RUB", 7L, CashFlowAccount.merchant, "payout", 2L, CashFlowAccount.system, "settlement", PaymentChangeType.payout);
        cashFlows.add(cashFlowPayoutFixedFee);
        CashFlow cashFlowAdjustmentAmount = createCashFlow(1L, 1000L, "RUB", 1L, CashFlowAccount.provider, "settlement", 2L, CashFlowAccount.merchant, "settlement", PaymentChangeType.adjustment);
        cashFlowAdjustmentAmount.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentAmount);
        CashFlow cashFlowAdjustmentFee = createCashFlow(1L, 10L, "RUB", 2L, CashFlowAccount.merchant, "settlement", 2L, CashFlowAccount.system, "settlement", PaymentChangeType.adjustment);
        cashFlowAdjustmentFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentFee);
        CashFlow cashFlowAdjustmentExternalIncomeFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 3L, CashFlowAccount.external, "income", PaymentChangeType.adjustment);
        cashFlowAdjustmentExternalIncomeFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentExternalIncomeFee);
        CashFlow cashFlowAdjustmentExternalOutcomeFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 4L, CashFlowAccount.external, "outcome", PaymentChangeType.adjustment);
        cashFlowAdjustmentExternalOutcomeFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentExternalOutcomeFee);
        CashFlow cashFlowAdjustmentProviderFee = createCashFlow(1L, 3L, "RUB", 2L, CashFlowAccount.system, "settlement", 5L, CashFlowAccount.provider, "settlement", PaymentChangeType.adjustment);
        cashFlowAdjustmentProviderFee.setAdjFlowType(AdjustmentCashFlowType.new_cash_flow);
        cashFlows.add(cashFlowAdjustmentProviderFee);

        cashFlowDao.save(cashFlows);

        assertEquals(cashFlowPaymentAmount.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payment_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payment_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentExternalIncomeFee.getAmount() + cashFlowPaymentExternalOutcomeFee.getAmount(), (long) jdbcTemplate.queryForObject("SELECT nw.get_payment_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentProviderFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payment_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPaymentGuaranteeDeposit.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payment_guarantee_deposit(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(cashFlowRefundAmount.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_refund_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowRefundFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_refund_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowRefundExternalIncomeFee.getAmount() + cashFlowRefundExternalOutcomeFee.getAmount(), (long) jdbcTemplate.queryForObject("SELECT nw.get_refund_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowRefundProviderFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_refund_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(cashFlowPayoutAmount.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payout_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPayoutFixedFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payout_fixed_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowPayoutFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_payout_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(cashFlowAdjustmentAmount.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_adjustment_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowAdjustmentFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_adjustment_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowAdjustmentExternalIncomeFee.getAmount() + cashFlowAdjustmentExternalOutcomeFee.getAmount(), (long) jdbcTemplate.queryForObject("SELECT nw.get_adjustment_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(cashFlowAdjustmentProviderFee.getAmount(), jdbcTemplate.queryForObject("SELECT nw.get_adjustment_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
    }

    @Test
    public void whenDataNotFoundCashFlowAggregateFunctionTest() {
        jdbcTemplate.execute("truncate table nw.cash_flow cascade");
        CashFlow notCashFlow = random(CashFlow.class, "objId");
        notCashFlow.setObjId(1L);
        cashFlowDao.save(Collections.singletonList(notCashFlow));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payment_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payment_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payment_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payment_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payment_guarantee_deposit(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_refund_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_refund_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_refund_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_refund_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payout_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payout_fixed_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_payout_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));

        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_adjustment_amount(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_adjustment_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_adjustment_external_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
        assertEquals(0L, (long) jdbcTemplate.queryForObject("SELECT nw.get_adjustment_provider_fee(nw.cash_flow.*) FROM nw.cash_flow WHERE obj_id = 1", new SingleColumnRowMapper<>(Long.class)));
    }

    @Test
    public void cashFlowDaoTest() {
        jdbcTemplate.execute("truncate table nw.payment cascade");
        jdbcTemplate.execute("truncate table nw.cash_flow cascade");
        Long pmntId = 123L;
        List<CashFlow> cashFlowList = randomListOf(100, CashFlow.class);
        cashFlowList.forEach(cf -> {
            cf.setObjId(pmntId);
            cf.setAmount((long) new Random().nextInt(100));
            cf.setObjType(PaymentChangeType.payment);
            cf.setAdjFlowType(null);
            cf.setSourceAccountTypeValue("settlement");
            if (cf.getDestinationAccountType() == com.rbkmoney.newway.domain.enums.CashFlowAccount.external) {
                cf.setDestinationAccountTypeValue("income");
            } else {
                cf.setDestinationAccountTypeValue("settlement");
            }
        });
        cashFlowDao.save(cashFlowList);
        List<CashFlow> byObjId = cashFlowDao.getByObjId(pmntId, PaymentChangeType.payment);
        assertEquals(new HashSet(byObjId), new HashSet(cashFlowList));
    }

    @Test
    public void challengeDaoTest() {
        jdbcTemplate.execute("truncate table nw.challenge cascade");
        Challenge challenge = random(Challenge.class);
        challenge.setCurrent(true);
        Long id = challengeDao.save(challenge);
        challenge.setId(id);
        assertEquals(challenge, challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
        challengeDao.updateNotCurrent(challenge.getIdentityId(), challenge.getChallengeId());
        assertNull(challengeDao.get(challenge.getIdentityId(), challenge.getChallengeId()));
    }

    @Test
    public void identityDaoTest() {
        jdbcTemplate.execute("truncate table nw.identity cascade");
        Identity identity = random(Identity.class);
        identity.setCurrent(true);
        Long id = identityDao.save(identity);
        identity.setId(id);
        assertEquals(identity, identityDao.get(identity.getIdentityId()));
        identityDao.updateNotCurrent(identity.getIdentityId());
        assertNull(identityDao.get(identity.getIdentityId()));
    }

    @Test
    public void adjustmentDaoTest() {
        jdbcTemplate.execute("truncate table nw.adjustment cascade");
        Adjustment adjustment = random(Adjustment.class);
        adjustment.setCurrent(true);
        adjustmentDao.save(adjustment);
        assertEquals(adjustment.getPartyId(), adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()).getPartyId());
        adjustmentDao.updateNotCurrent(adjustment.getId());
        Assert.assertNull(adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()));
    }

    @Test
    public void invoiceCartDaoTest() {
        jdbcTemplate.execute("truncate table nw.invoice cascade");
        jdbcTemplate.execute("truncate table nw.invoice_cart cascade");
        Invoice invoice = random(Invoice.class);
        invoice.setCurrent(true);
        Long invId = invoice.getId();
        invoiceDao.saveBatch(Collections.singletonList(invoice));
        List<InvoiceCart> invoiceCarts = randomListOf(10, InvoiceCart.class);
        invoiceCarts.forEach(ic -> {
            ic.setInvId(invId);
        });
        invoiceCartDao.save(invoiceCarts);
        List<InvoiceCart> byInvId = invoiceCartDao.getByInvId(invId);
        assertEquals(new HashSet(invoiceCarts), new HashSet(byInvId));
    }

    @Test
    public void paymentDaoTest() {
        jdbcTemplate.execute("truncate table nw.payment cascade");
        Payment payment = random(Payment.class, "id");
        payment.setCurrent(false);
        Payment paymentTwo = random(Payment.class, "id");
        paymentTwo.setCurrent(false);
        paymentTwo.setInvoiceId(payment.getInvoiceId());
        paymentTwo.setPaymentId(payment.getPaymentId());
        paymentDao.saveBatch(Arrays.asList(payment, paymentTwo));
        paymentDao.switchCurrent(Collections.singletonList(new InvoicingKey(payment.getInvoiceId(), payment.getPaymentId(), InvoicingType.PAYMENT)));
        Payment paymentGet = paymentDao.get(payment.getInvoiceId(), payment.getPaymentId());
        paymentGet.setId(null);
        paymentTwo.setCurrent(true);
        assertEquals(paymentTwo, paymentGet);
    }

    @Test
    public void refundDaoTest() {
        jdbcTemplate.execute("truncate table nw.refund cascade");
        Refund refund = random(Refund.class);
        refund.setCurrent(true);
        refundDao.save(refund);
        Refund refundGet = refundDao.get(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId());
        assertEquals(refund, refundGet);
        refundDao.updateNotCurrent(refund.getId());
        Assert.assertNull(refundDao.get(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId()));
    }

    @Test
    public void contractAdjustmentDaoTest() {
        jdbcTemplate.execute("truncate table nw.contract_adjustment cascade");
        jdbcTemplate.execute("truncate table nw.contract cascade");
        Contract contract = random(Contract.class);
        contract.setCurrent(true);
        Long cntrctId = contractDao.save(contract);
        List<ContractAdjustment> contractAdjustments = randomListOf(10, ContractAdjustment.class);
        contractAdjustments.forEach(ca -> ca.setCntrctId(cntrctId));
        contractAdjustmentDao.save(contractAdjustments);
        List<ContractAdjustment> byCntrctId = contractAdjustmentDao.getByCntrctId(cntrctId);
        assertEquals(new HashSet(contractAdjustments), new HashSet(byCntrctId));
    }

    @Test
    public void contractDaoTest() {
        jdbcTemplate.execute("truncate table nw.contract cascade");
        Contract contract = random(Contract.class);
        contract.setCurrent(true);
        contractDao.save(contract);
        Contract contractGet = contractDao.get(contract.getPartyId(), contract.getContractId());
        assertEquals(contract, contractGet);
        List<Contract> contracts = contractDao.getByPartyId(contract.getPartyId());
        assertEquals(1, contracts.size());
        assertEquals(contract, contracts.get(0));
        contractDao.updateNotCurrent(contract.getPartyId(), contract.getContractId());
        Assert.assertNull(contractDao.get(contract.getPartyId(), contract.getContractId()));
    }

    @Test
    public void contractorDaoTest() {
        jdbcTemplate.execute("truncate table nw.contractor cascade");
        Contractor contractor = random(Contractor.class);
        contractor.setCurrent(true);
        contractorDao.save(contractor);
        Contractor contractorGet = contractorDao.get(contractor.getPartyId(), contractor.getContractorId());
        assertEquals(contractor, contractorGet);
        List<Contractor> contractors = contractorDao.getByPartyId(contractor.getPartyId());
        assertEquals(1, contractors.size());
        assertEquals(contractor, contractors.get(0));
        contractorDao.updateNotCurrent(contractor.getPartyId(), contractor.getContractorId());
        Assert.assertNull(contractorDao.get(contractor.getPartyId(), contractor.getContractorId()));
    }

    @Test
    public void partyDaoTest() {
        jdbcTemplate.execute("truncate table nw.party cascade");
        Party party = random(Party.class);
        party.setCurrent(true);
        partyDao.save(party);
        Party partyGet = partyDao.get(party.getPartyId());
        assertEquals(party, partyGet);
        partyDao.updateNotCurrent(party.getPartyId());
        Assert.assertNull(partyDao.get(party.getPartyId()));
        assertEquals(partyDao.getLastEventId(), party.getEventId());
    }

    @Test
    public void payoutToolDaoTest() {
        jdbcTemplate.execute("truncate table nw.contract cascade");
        jdbcTemplate.execute("truncate table nw.payout_tool cascade");
        Contract contract = random(Contract.class);
        contract.setCurrent(true);
        Long cntrctId = contractDao.save(contract);
        List<PayoutTool> payoutTools = randomListOf(10, PayoutTool.class);
        payoutTools.forEach(pt -> pt.setCntrctId(cntrctId));
        payoutToolDao.save(payoutTools);
        List<PayoutTool> byCntrctId = payoutToolDao.getByCntrctId(cntrctId);
        assertEquals(new HashSet(payoutTools), new HashSet(byCntrctId));
    }

    @Test
    public void shopDaoTest() {
        jdbcTemplate.execute("truncate table nw.shop cascade");
        Shop shop = random(Shop.class);
        shop.setCurrent(true);
        shopDao.save(shop);
        Shop shopGet = shopDao.get(shop.getPartyId(), shop.getShopId());
        assertEquals(shop, shopGet);
        List<Shop> shops = shopDao.getByPartyId(shop.getPartyId());
        assertEquals(1, shops.size());
        assertEquals(shop, shops.get(0));
        shopDao.updateNotCurrent(shop.getPartyId(), shop.getShopId());
        Assert.assertNull(shopDao.get(shop.getPartyId(), shop.getShopId()));
    }

    @Test
    public void payoutDaoTest() {
        jdbcTemplate.execute("truncate table nw.payout cascade");
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        payoutDao.save(payout);
        Payout payoutGet = payoutDao.get(payout.getPayoutId());
        assertEquals(payout, payoutGet);
        payoutDao.updateNotCurrent(payout.getPayoutId());
        Assert.assertNull(payoutDao.get(payout.getPayoutId()));
        assertEquals(payoutDao.getLastEventId(), payout.getEventId());
    }

    @Test
    public void payoutSummaryDaoTest() {
        jdbcTemplate.execute("truncate table nw.payout_summary cascade");
        Payout payout = random(Payout.class);
        payout.setCurrent(true);
        Long pytId = payoutDao.save(payout);
        List<PayoutSummary> payoutSummaries = randomListOf(10, PayoutSummary.class);
        payoutSummaries.forEach(pt -> pt.setPytId(pytId));
        payoutSummaryDao.save(payoutSummaries);
        List<PayoutSummary> byPytId = payoutSummaryDao.getByPytId(pytId);
        assertEquals(new HashSet(payoutSummaries), new HashSet(byPytId));
    }

    @Test
    public void rateDaoTest() {
        jdbcTemplate.execute("truncate table nw.rate cascade");
        Rate rate = random(Rate.class);
        rate.setCurrent(true);

        Long id = rateDao.save(rate);
        rate.setId(id);
        assertEquals(
                rate,
                jdbcTemplate.queryForObject(
                        "SELECT * FROM nw.rate WHERE id = ? ",
                        new Object[]{id},
                        new BeanPropertyRowMapper(Rate.class)
                )
        );

        List<Long> ids = rateDao.getIds(rate.getSourceId());
        assertNotNull(ids);
        assertFalse(ids.isEmpty());
        assertEquals(1, ids.size());
        assertEquals(id, ids.get(0));

        rateDao.updateNotCurrent(Collections.singletonList(id));
        try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM nw.rate AS rate WHERE rate.id = ? AND rate.current",
                    new Object[]{id},
                    new BeanPropertyRowMapper(Rate.class)
            );
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof EmptyResultDataAccessException);
        }
    }

    @Test
    public void sourceDaoTest() {
        jdbcTemplate.execute("truncate table nw.source cascade");
        Source source = random(Source.class);
        source.setCurrent(true);
        Long id = sourceDao.save(source);
        source.setId(id);
        assertEquals(source, sourceDao.get(source.getSourceId()));
        sourceDao.updateNotCurrent(source.getSourceId());
        Assert.assertNull(sourceDao.get(source.getSourceId()));
    }

    @Test
    public void walletDaoTest() {
        jdbcTemplate.execute("truncate table nw.wallet cascade");
        Wallet wallet = random(Wallet.class);
        wallet.setCurrent(true);
        Long id = walletDao.save(wallet);
        wallet.setId(id);
        assertEquals(wallet, walletDao.get(wallet.getWalletId()));
        walletDao.updateNotCurrent(wallet.getWalletId());
        Assert.assertNull(walletDao.get(wallet.getWalletId()));
    }

    @Test
    public void withdrawalDaoTest() {
        jdbcTemplate.execute("truncate table nw.withdrawal cascade");
        Withdrawal withdrawal = random(Withdrawal.class);
        withdrawal.setCurrent(true);
        Long id = withdrawalDao.save(withdrawal);
        withdrawal.setId(id);
        assertEquals(withdrawal, withdrawalDao.get(withdrawal.getWithdrawalId()));
        withdrawalDao.updateNotCurrent(withdrawal.getWithdrawalId());
        assertNull(withdrawalDao.get(withdrawal.getWithdrawalId()));
    }

    @Test
    public void withdrawalSessionDao() {
        jdbcTemplate.execute("truncate table nw.withdrawal_session cascade");
        WithdrawalSession withdrawalSession = random(WithdrawalSession.class);
        withdrawalSession.setCurrent(true);
        Long id = withdrawalSessionDao.save(withdrawalSession);
        withdrawalSession.setId(id);
        assertEquals(withdrawalSession, withdrawalSessionDao.get(withdrawalSession.getWithdrawalSessionId()));
        withdrawalSessionDao.updateNotCurrent(withdrawalSession.getWithdrawalSessionId());
        assertNull(withdrawalSessionDao.get(withdrawalSession.getWithdrawalSessionId()));
    }

    @Test
    public void getIntHashTest() {
        Integer javaHash = HashUtil.getIntHash("kek");
        Integer postgresHash = jdbcTemplate.queryForObject("select ('x0'||substr(md5('kek'), 1, 7))::bit(32)::int", Integer.class);
        assertEquals(javaHash, postgresHash);
    }

    @Test
    public void constraintTests() {
        jdbcTemplate.execute("truncate table nw.adjustment cascade");
        Adjustment adjustment = random(Adjustment.class);
        adjustment.setChangeId(1);
        adjustment.setSequenceId(1L);
        adjustment.setInvoiceId("1");
        adjustment.setPartyId("1");
        adjustment.setCurrent(true);
        adjustmentDao.save(adjustment);

        assertEquals("1", adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()).getPartyId());

        adjustment.setPartyId("2");

        adjustmentDao.save(adjustment);

        assertEquals("1", adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()).getPartyId());
    }

    @Test
    public void idsGeneratorTest() {
        List<Long> list = idsGeneratorDao.get(100);
        assertEquals(100, list.size());
        assertEquals(99,list.get(99) - list.get(0));
    }

    @Test
    public void contractIdsGeneratorTest() {
        List<Long> list = contractIdsGeneratorDao.get(100);
        assertEquals(100, list.size());
        assertEquals(99,list.get(99) - list.get(0));
    }

    @Test
    public void recurrentPaymentToolDaoTest(){
        jdbcTemplate.execute("truncate table nw.recurrent_payment_tool cascade");
        RecurrentPaymentTool recurrentPaymentTool = random(RecurrentPaymentTool.class);
        recurrentPaymentTool.setCurrent(true);
        Optional<Long> id = recurrentPaymentToolDao.save(recurrentPaymentTool);
        assertTrue(id.isPresent());
        recurrentPaymentTool.setId(id.get());
        assertEquals(recurrentPaymentTool, recurrentPaymentToolDao.get(recurrentPaymentTool.getRecurrentPaymentToolId()));
        recurrentPaymentToolDao.updateNotCurrent(recurrentPaymentTool.getId());
        assertNull(recurrentPaymentToolDao.get(recurrentPaymentTool.getRecurrentPaymentToolId()));
    }
}
