/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables;


import com.rbkmoney.newway.domain.Keys;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.newway.domain.enums.RefundStatus;
import com.rbkmoney.newway.domain.tables.records.RefundRecord;

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
public class Refund extends TableImpl<RefundRecord> {

    private static final long serialVersionUID = -272283746;

    /**
     * The reference instance of <code>nw.refund</code>
     */
    public static final Refund REFUND = new Refund();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RefundRecord> getRecordType() {
        return RefundRecord.class;
    }

    /**
     * The column <code>nw.refund.id</code>.
     */
    public final TableField<RefundRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('nw.refund_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>nw.refund.event_id</code>.
     */
    public final TableField<RefundRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.refund.event_created_at</code>.
     */
    public final TableField<RefundRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.refund.domain_revision</code>.
     */
    public final TableField<RefundRecord, Long> DOMAIN_REVISION = createField("domain_revision", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.refund.refund_id</code>.
     */
    public final TableField<RefundRecord, String> REFUND_ID = createField("refund_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.refund.payment_id</code>.
     */
    public final TableField<RefundRecord, String> PAYMENT_ID = createField("payment_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.refund.invoice_id</code>.
     */
    public final TableField<RefundRecord, String> INVOICE_ID = createField("invoice_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.refund.party_id</code>.
     */
    public final TableField<RefundRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.refund.shop_id</code>.
     */
    public final TableField<RefundRecord, String> SHOP_ID = createField("shop_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.refund.created_at</code>.
     */
    public final TableField<RefundRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.refund.status</code>.
     */
    public final TableField<RefundRecord, RefundStatus> STATUS = createField("status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.RefundStatus.class), this, "");

    /**
     * The column <code>nw.refund.status_failed_failure</code>.
     */
    public final TableField<RefundRecord, String> STATUS_FAILED_FAILURE = createField("status_failed_failure", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.refund.amount</code>.
     */
    public final TableField<RefundRecord, Long> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>nw.refund.currency_code</code>.
     */
    public final TableField<RefundRecord, String> CURRENCY_CODE = createField("currency_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.refund.reason</code>.
     */
    public final TableField<RefundRecord, String> REASON = createField("reason", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.refund.wtime</code>.
     */
    public final TableField<RefundRecord, LocalDateTime> WTIME = createField("wtime", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>nw.refund.current</code>.
     */
    public final TableField<RefundRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>nw.refund</code> table reference
     */
    public Refund() {
        this("refund", null);
    }

    /**
     * Create an aliased <code>nw.refund</code> table reference
     */
    public Refund(String alias) {
        this(alias, REFUND);
    }

    private Refund(String alias, Table<RefundRecord> aliased) {
        this(alias, aliased, null);
    }

    private Refund(String alias, Table<RefundRecord> aliased, Field<?>[] parameters) {
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
    public Identity<RefundRecord, Long> getIdentity() {
        return Keys.IDENTITY_REFUND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<RefundRecord> getPrimaryKey() {
        return Keys.REFUND_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<RefundRecord>> getKeys() {
        return Arrays.<UniqueKey<RefundRecord>>asList(Keys.REFUND_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Refund as(String alias) {
        return new Refund(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Refund rename(String name) {
        return new Refund(name, null);
    }
}
