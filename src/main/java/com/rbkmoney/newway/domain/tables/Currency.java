/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables;


import com.rbkmoney.newway.domain.Keys;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.newway.domain.tables.records.CurrencyRecord;

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
public class Currency extends TableImpl<CurrencyRecord> {

    private static final long serialVersionUID = 271333018;

    /**
     * The reference instance of <code>nw.currency</code>
     */
    public static final Currency CURRENCY = new Currency();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CurrencyRecord> getRecordType() {
        return CurrencyRecord.class;
    }

    /**
     * The column <code>nw.currency.id</code>.
     */
    public final TableField<CurrencyRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('nw.currency_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>nw.currency.version_id</code>.
     */
    public final TableField<CurrencyRecord, Long> VERSION_ID = createField("version_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.currency.currency_ref_id</code>.
     */
    public final TableField<CurrencyRecord, String> CURRENCY_REF_ID = createField("currency_ref_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.currency.name</code>.
     */
    public final TableField<CurrencyRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.currency.symbolic_code</code>.
     */
    public final TableField<CurrencyRecord, String> SYMBOLIC_CODE = createField("symbolic_code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.currency.numeric_code</code>.
     */
    public final TableField<CurrencyRecord, Short> NUMERIC_CODE = createField("numeric_code", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>nw.currency.exponent</code>.
     */
    public final TableField<CurrencyRecord, Short> EXPONENT = createField("exponent", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>nw.currency.wtime</code>.
     */
    public final TableField<CurrencyRecord, LocalDateTime> WTIME = createField("wtime", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("timezone('utc'::text, now())", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>nw.currency.current</code>.
     */
    public final TableField<CurrencyRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>nw.currency</code> table reference
     */
    public Currency() {
        this("currency", null);
    }

    /**
     * Create an aliased <code>nw.currency</code> table reference
     */
    public Currency(String alias) {
        this(alias, CURRENCY);
    }

    private Currency(String alias, Table<CurrencyRecord> aliased) {
        this(alias, aliased, null);
    }

    private Currency(String alias, Table<CurrencyRecord> aliased, Field<?>[] parameters) {
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
    public Identity<CurrencyRecord, Long> getIdentity() {
        return Keys.IDENTITY_CURRENCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CurrencyRecord> getPrimaryKey() {
        return Keys.CURRENCY_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CurrencyRecord>> getKeys() {
        return Arrays.<UniqueKey<CurrencyRecord>>asList(Keys.CURRENCY_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Currency as(String alias) {
        return new Currency(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Currency rename(String name) {
        return new Currency(name, null);
    }
}