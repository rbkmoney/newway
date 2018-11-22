/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables;


import com.rbkmoney.newway.domain.Keys;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.newway.domain.enums.DepositStatus;
import com.rbkmoney.newway.domain.enums.DepositTransferStatus;
import com.rbkmoney.newway.domain.tables.records.DepositRecord;

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
public class Deposit extends TableImpl<DepositRecord> {

    private static final long serialVersionUID = -1413031506;

    /**
     * The reference instance of <code>nw.deposit</code>
     */
    public static final Deposit DEPOSIT = new Deposit();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DepositRecord> getRecordType() {
        return DepositRecord.class;
    }

    /**
     * The column <code>nw.deposit.id</code>.
     */
    public final TableField<DepositRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('nw.deposit_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>nw.deposit.event_id</code>.
     */
    public final TableField<DepositRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.event_created_at</code>.
     */
    public final TableField<DepositRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.event_occured_at</code>.
     */
    public final TableField<DepositRecord, LocalDateTime> EVENT_OCCURED_AT = createField("event_occured_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.sequence_id</code>.
     */
    public final TableField<DepositRecord, Integer> SEQUENCE_ID = createField("sequence_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.source_id</code>.
     */
    public final TableField<DepositRecord, String> SOURCE_ID = createField("source_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.wallet_id</code>.
     */
    public final TableField<DepositRecord, String> WALLET_ID = createField("wallet_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.deposit_id</code>.
     */
    public final TableField<DepositRecord, String> DEPOSIT_ID = createField("deposit_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.amount</code>.
     */
    public final TableField<DepositRecord, Long> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.fee</code>.
     */
    public final TableField<DepositRecord, Long> FEE = createField("fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>nw.deposit.provider_fee</code>.
     */
    public final TableField<DepositRecord, Long> PROVIDER_FEE = createField("provider_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>nw.deposit.currency_code</code>.
     */
    public final TableField<DepositRecord, String> CURRENCY_CODE = createField("currency_code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.deposit.deposit_status</code>.
     */
    public final TableField<DepositRecord, DepositStatus> DEPOSIT_STATUS = createField("deposit_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.DepositStatus.class), this, "");

    /**
     * The column <code>nw.deposit.deposit_transfer_status</code>.
     */
    public final TableField<DepositRecord, DepositTransferStatus> DEPOSIT_TRANSFER_STATUS = createField("deposit_transfer_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.DepositTransferStatus.class), this, "");

    /**
     * The column <code>nw.deposit.wtime</code>.
     */
    public final TableField<DepositRecord, LocalDateTime> WTIME = createField("wtime", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("timezone('utc'::text, now())", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>nw.deposit.current</code>.
     */
    public final TableField<DepositRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>nw.deposit</code> table reference
     */
    public Deposit() {
        this("deposit", null);
    }

    /**
     * Create an aliased <code>nw.deposit</code> table reference
     */
    public Deposit(String alias) {
        this(alias, DEPOSIT);
    }

    private Deposit(String alias, Table<DepositRecord> aliased) {
        this(alias, aliased, null);
    }

    private Deposit(String alias, Table<DepositRecord> aliased, Field<?>[] parameters) {
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
    public Identity<DepositRecord, Long> getIdentity() {
        return Keys.IDENTITY_DEPOSIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DepositRecord> getPrimaryKey() {
        return Keys.DEPOSIT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DepositRecord>> getKeys() {
        return Arrays.<UniqueKey<DepositRecord>>asList(Keys.DEPOSIT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Deposit as(String alias) {
        return new Deposit(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Deposit rename(String name) {
        return new Deposit(name, null);
    }
}