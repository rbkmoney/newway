/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables;


import com.rbkmoney.newway.domain.Keys;
import com.rbkmoney.newway.domain.Nw;
import com.rbkmoney.newway.domain.enums.ContractStatus;
import com.rbkmoney.newway.domain.enums.RepresentativeDocument;
import com.rbkmoney.newway.domain.tables.records.ContractRecord;

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
public class Contract extends TableImpl<ContractRecord> {

    private static final long serialVersionUID = -441277304;

    /**
     * The reference instance of <code>nw.contract</code>
     */
    public static final Contract CONTRACT = new Contract();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ContractRecord> getRecordType() {
        return ContractRecord.class;
    }

    /**
     * The column <code>nw.contract.id</code>.
     */
    public final TableField<ContractRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('nw.contract_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>nw.contract.event_id</code>.
     */
    public final TableField<ContractRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>nw.contract.event_created_at</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.contract.contract_id</code>.
     */
    public final TableField<ContractRecord, String> CONTRACT_ID = createField("contract_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.contract.party_id</code>.
     */
    public final TableField<ContractRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>nw.contract.payment_institution_id</code>.
     */
    public final TableField<ContractRecord, Integer> PAYMENT_INSTITUTION_ID = createField("payment_institution_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>nw.contract.created_at</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>nw.contract.valid_since</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> VALID_SINCE = createField("valid_since", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.valid_until</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> VALID_UNTIL = createField("valid_until", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.status</code>.
     */
    public final TableField<ContractRecord, ContractStatus> STATUS = createField("status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.ContractStatus.class), this, "");

    /**
     * The column <code>nw.contract.status_terminated_at</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> STATUS_TERMINATED_AT = createField("status_terminated_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.terms_id</code>.
     */
    public final TableField<ContractRecord, Integer> TERMS_ID = createField("terms_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>nw.contract.legal_agreement_signed_at</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> LEGAL_AGREEMENT_SIGNED_AT = createField("legal_agreement_signed_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.legal_agreement_id</code>.
     */
    public final TableField<ContractRecord, String> LEGAL_AGREEMENT_ID = createField("legal_agreement_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.contract.legal_agreement_valid_until</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> LEGAL_AGREEMENT_VALID_UNTIL = createField("legal_agreement_valid_until", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.report_act_schedule_id</code>.
     */
    public final TableField<ContractRecord, Integer> REPORT_ACT_SCHEDULE_ID = createField("report_act_schedule_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>nw.contract.report_act_signer_position</code>.
     */
    public final TableField<ContractRecord, String> REPORT_ACT_SIGNER_POSITION = createField("report_act_signer_position", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.contract.report_act_signer_full_name</code>.
     */
    public final TableField<ContractRecord, String> REPORT_ACT_SIGNER_FULL_NAME = createField("report_act_signer_full_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.contract.report_act_signer_document</code>.
     */
    public final TableField<ContractRecord, RepresentativeDocument> REPORT_ACT_SIGNER_DOCUMENT = createField("report_act_signer_document", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.newway.domain.enums.RepresentativeDocument.class), this, "");

    /**
     * The column <code>nw.contract.report_act_signer_doc_power_of_attorney_signed_at</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> REPORT_ACT_SIGNER_DOC_POWER_OF_ATTORNEY_SIGNED_AT = createField("report_act_signer_doc_power_of_attorney_signed_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.report_act_signer_doc_power_of_attorney_legal_agreement_id</code>.
     */
    public final TableField<ContractRecord, String> REPORT_ACT_SIGNER_DOC_POWER_OF_ATTORNEY_LEGAL_AGREEMENT_ID = createField("report_act_signer_doc_power_of_attorney_legal_agreement_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.contract.report_act_signer_doc_power_of_attorney_valid_until</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> REPORT_ACT_SIGNER_DOC_POWER_OF_ATTORNEY_VALID_UNTIL = createField("report_act_signer_doc_power_of_attorney_valid_until", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>nw.contract.contractor_id</code>.
     */
    public final TableField<ContractRecord, String> CONTRACTOR_ID = createField("contractor_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>nw.contract.wtime</code>.
     */
    public final TableField<ContractRecord, LocalDateTime> WTIME = createField("wtime", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false).defaultValue(org.jooq.impl.DSL.field("now()", org.jooq.impl.SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>nw.contract.current</code>.
     */
    public final TableField<ContractRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>nw.contract</code> table reference
     */
    public Contract() {
        this("contract", null);
    }

    /**
     * Create an aliased <code>nw.contract</code> table reference
     */
    public Contract(String alias) {
        this(alias, CONTRACT);
    }

    private Contract(String alias, Table<ContractRecord> aliased) {
        this(alias, aliased, null);
    }

    private Contract(String alias, Table<ContractRecord> aliased, Field<?>[] parameters) {
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
    public Identity<ContractRecord, Long> getIdentity() {
        return Keys.IDENTITY_CONTRACT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ContractRecord> getPrimaryKey() {
        return Keys.CONTRACT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ContractRecord>> getKeys() {
        return Arrays.<UniqueKey<ContractRecord>>asList(Keys.CONTRACT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contract as(String alias) {
        return new Contract(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Contract rename(String name) {
        return new Contract(name, null);
    }
}
