/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables;


import com.rbkmoney.newway.domain.Keys;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.newway.domain.tables.records.WalletRecord;

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
public class Wallet extends TableImpl<WalletRecord> {

    private static final long serialVersionUID = -1143589801;

    /**
     * The reference instance of <code>nw.wallet</code>
     */
    public static final Wallet WALLET = new Wallet();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WalletRecord> getRecordType() {
        return WalletRecord.class;
    }

    /**
     * The column <code>nw.wallet.id</code>.
     */
    public final TableField<WalletRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('nw.wallet_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>nw.wallet.event_id</code>.
     */
    public final TableField<WalletRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.wallet.event_created_at</code>.
     */
    public final TableField<WalletRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.wallet.event_occured_at</code>.
     */
    public final TableField<WalletRecord, LocalDateTime> EVENT_OCCURED_AT = createField("event_occured_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.wallet.sequence_id</code>.
     */
    public final TableField<WalletRecord, Integer> SEQUENCE_ID = createField("sequence_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>nw.wallet.wallet_id</code>.
     */
    public final TableField<WalletRecord, String> WALLET_ID = createField("wallet_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.wallet.wallet_name</code>.
     */
    public final TableField<WalletRecord, String> WALLET_NAME = createField("wallet_name", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.wallet.identity_id</code>.
     */
    public final TableField<WalletRecord, String> IDENTITY_ID = createField("identity_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.wallet.currency_code</code>.
     */
    public final TableField<WalletRecord, String> CURRENCY_CODE = createField("currency_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.wallet.wtime</code>.
     */
    public final TableField<WalletRecord, LocalDateTime> WTIME = createField("wtime", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("timezone('utc'::text, now())", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>nw.wallet.current</code>.
     */
    public final TableField<WalletRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>nw.wallet</code> table reference
     */
    public Wallet() {
        this("wallet", null);
    }

    /**
     * Create an aliased <code>nw.wallet</code> table reference
     */
    public Wallet(String alias) {
        this(alias, WALLET);
    }

    private Wallet(String alias, Table<WalletRecord> aliased) {
        this(alias, aliased, null);
    }

    private Wallet(String alias, Table<WalletRecord> aliased, Field<?>[] parameters) {
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
    public Identity<WalletRecord, Long> getIdentity() {
        return Keys.IDENTITY_WALLET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<WalletRecord> getPrimaryKey() {
        return Keys.WALLET_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<WalletRecord>> getKeys() {
        return Arrays.<UniqueKey<WalletRecord>>asList(Keys.WALLET_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Wallet as(String alias) {
        return new Wallet(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Wallet rename(String name) {
        return new Wallet(name, null);
    }
}
