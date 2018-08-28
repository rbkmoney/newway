/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables;


import com.rbkmoney.newway.domain.Keys;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.newway.domain.enums.PayoutAccountType;
import com.rbkmoney.newway.domain.enums.PayoutPaidStatusDetails;
import com.rbkmoney.newway.domain.enums.PayoutStatus;
import com.rbkmoney.newway.domain.enums.PayoutType;
import com.rbkmoney.newway.domain.enums.UserType;
import com.rbkmoney.newway.domain.tables.records.PayoutRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Payout extends TableImpl<PayoutRecord> {

    private static final long serialVersionUID = 1391130092;

    /**
     * The reference instance of <code>nw.payout</code>
     */
    public static final Payout PAYOUT = new Payout();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PayoutRecord> getRecordType() {
        return PayoutRecord.class;
    }

    /**
     * The column <code>nw.payout.id</code>.
     */
    public final TableField<PayoutRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('nw.payout_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>nw.payout.event_id</code>.
     */
    public final TableField<PayoutRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.payout.event_created_at</code>.
     */
    public final TableField<PayoutRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.payout.payout_id</code>.
     */
    public final TableField<PayoutRecord, String> PAYOUT_ID = createField("payout_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.payout.party_id</code>.
     */
    public final TableField<PayoutRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.payout.shop_id</code>.
     */
    public final TableField<PayoutRecord, String> SHOP_ID = createField("shop_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.payout.contract_id</code>.
     */
    public final TableField<PayoutRecord, String> CONTRACT_ID = createField("contract_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.payout.created_at</code>.
     */
    public final TableField<PayoutRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.payout.status</code>.
     */
    public final TableField<PayoutRecord, PayoutStatus> STATUS = createField("status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.PayoutStatus.class), this, "");

    /**
     * The column <code>nw.payout.status_paid_details</code>.
     */
    public final TableField<PayoutRecord, PayoutPaidStatusDetails> STATUS_PAID_DETAILS = createField("status_paid_details", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.PayoutPaidStatusDetails.class), this, "");

    /**
     * The column <code>nw.payout.status_paid_details_card_provider_name</code>.
     */
    public final TableField<PayoutRecord, String> STATUS_PAID_DETAILS_CARD_PROVIDER_NAME = createField("status_paid_details_card_provider_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.status_paid_details_card_provider_transaction_id</code>.
     */
    public final TableField<PayoutRecord, String> STATUS_PAID_DETAILS_CARD_PROVIDER_TRANSACTION_ID = createField("status_paid_details_card_provider_transaction_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.status_cancelled_user_info_id</code>.
     */
    public final TableField<PayoutRecord, String> STATUS_CANCELLED_USER_INFO_ID = createField("status_cancelled_user_info_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.status_cancelled_user_info_type</code>.
     */
    public final TableField<PayoutRecord, UserType> STATUS_CANCELLED_USER_INFO_TYPE = createField("status_cancelled_user_info_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.UserType.class), this, "");

    /**
     * The column <code>nw.payout.status_cancelled_details</code>.
     */
    public final TableField<PayoutRecord, String> STATUS_CANCELLED_DETAILS = createField("status_cancelled_details", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.status_confirmed_user_info_id</code>.
     */
    public final TableField<PayoutRecord, String> STATUS_CONFIRMED_USER_INFO_ID = createField("status_confirmed_user_info_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.status_confirmed_user_info_type</code>.
     */
    public final TableField<PayoutRecord, UserType> STATUS_CONFIRMED_USER_INFO_TYPE = createField("status_confirmed_user_info_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.UserType.class), this, "");

    /**
     * The column <code>nw.payout.type</code>.
     */
    public final TableField<PayoutRecord, PayoutType> TYPE = createField("type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.PayoutType.class), this, "");

    /**
     * The column <code>nw.payout.type_card_token</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_CARD_TOKEN = createField("type_card_token", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_card_payment_system</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_CARD_PAYMENT_SYSTEM = createField("type_card_payment_system", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_card_bin</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_CARD_BIN = createField("type_card_bin", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_card_masked_pan</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_CARD_MASKED_PAN = createField("type_card_masked_pan", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_card_token_provider</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_CARD_TOKEN_PROVIDER = createField("type_card_token_provider", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_type</code>.
     */
    public final TableField<PayoutRecord, PayoutAccountType> TYPE_ACCOUNT_TYPE = createField("type_account_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.PayoutAccountType.class), this, "");

    /**
     * The column <code>nw.payout.type_account_russian_account</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_RUSSIAN_ACCOUNT = createField("type_account_russian_account", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_russian_bank_name</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_RUSSIAN_BANK_NAME = createField("type_account_russian_bank_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_russian_bank_post_account</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_RUSSIAN_BANK_POST_ACCOUNT = createField("type_account_russian_bank_post_account", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_russian_bank_bik</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_RUSSIAN_BANK_BIK = createField("type_account_russian_bank_bik", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_russian_inn</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_RUSSIAN_INN = createField("type_account_russian_inn", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_account_holder</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_ACCOUNT_HOLDER = createField("type_account_international_account_holder", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_bank_name</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_BANK_NAME = createField("type_account_international_bank_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_bank_address</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_BANK_ADDRESS = createField("type_account_international_bank_address", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_iban</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_IBAN = createField("type_account_international_iban", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_bic</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_BIC = createField("type_account_international_bic", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_local_bank_code</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_LOCAL_BANK_CODE = createField("type_account_international_local_bank_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_legal_entity_legal_name</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_LEGAL_ENTITY_LEGAL_NAME = createField("type_account_international_legal_entity_legal_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_legal_entity_trading_name</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_LEGAL_ENTITY_TRADING_NAME = createField("type_account_international_legal_entity_trading_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_legal_entity_registered_address</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_LEGAL_ENTITY_REGISTERED_ADDRESS = createField("type_account_international_legal_entity_registered_address", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_legal_entity_actual_address</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_LEGAL_ENTITY_ACTUAL_ADDRESS = createField("type_account_international_legal_entity_actual_address", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_international_legal_entity_registered_number</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_INTERNATIONAL_LEGAL_ENTITY_REGISTERED_NUMBER = createField("type_account_international_legal_entity_registered_number", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_purpose</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_PURPOSE = createField("type_account_purpose", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_legal_agreement_signed_at</code>.
     */
    public final TableField<PayoutRecord, LocalDateTime> TYPE_ACCOUNT_LEGAL_AGREEMENT_SIGNED_AT = createField("type_account_legal_agreement_signed_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.payout.type_account_legal_agreement_id</code>.
     */
    public final TableField<PayoutRecord, String> TYPE_ACCOUNT_LEGAL_AGREEMENT_ID = createField("type_account_legal_agreement_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.payout.type_account_legal_agreement_valid_until</code>.
     */
    public final TableField<PayoutRecord, LocalDateTime> TYPE_ACCOUNT_LEGAL_AGREEMENT_VALID_UNTIL = createField("type_account_legal_agreement_valid_until", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.payout.initiator_id</code>.
     */
    public final TableField<PayoutRecord, String> INITIATOR_ID = createField("initiator_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.payout.initiator_type</code>.
     */
    public final TableField<PayoutRecord, UserType> INITIATOR_TYPE = createField("initiator_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.UserType.class), this, "");

    /**
     * The column <code>nw.payout.wtime</code>.
     */
    public final TableField<PayoutRecord, LocalDateTime> WTIME = createField("wtime", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>nw.payout.current</code>.
     */
    public final TableField<PayoutRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>nw.payout</code> table reference
     */
    public Payout() {
        this("payout", null);
    }

    /**
     * Create an aliased <code>nw.payout</code> table reference
     */
    public Payout(String alias) {
        this(alias, PAYOUT);
    }

    private Payout(String alias, Table<PayoutRecord> aliased) {
        this(alias, aliased, null);
    }

    private Payout(String alias, Table<PayoutRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Nw.NW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PayoutRecord, Long> getIdentity() {
        return Keys.IDENTITY_PAYOUT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PayoutRecord> getPrimaryKey() {
        return Keys.PAYOUT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PayoutRecord>> getKeys() {
        return Arrays.<UniqueKey<PayoutRecord>>asList(Keys.PAYOUT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payout as(String alias) {
        return new Payout(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Payout rename(String name) {
        return new Payout(name, null);
    }
}
