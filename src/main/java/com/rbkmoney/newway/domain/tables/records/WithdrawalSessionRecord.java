/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables.records;


import com.rbkmoney.newway.domain.enums.BankCardPaymentSystem;
import com.rbkmoney.newway.domain.enums.WithdrawalSessionStatus;
import com.rbkmoney.newway.domain.tables.WithdrawalSession;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


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
public class WithdrawalSessionRecord extends UpdatableRecordImpl<WithdrawalSessionRecord> {

    private static final long serialVersionUID = -187301655;

    /**
     * Setter for <code>nw.withdrawal_session.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>nw.withdrawal_session.event_id</code>.
     */
    public void setEventId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.event_id</code>.
     */
    public Long getEventId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>nw.withdrawal_session.event_created_at</code>.
     */
    public void setEventCreatedAt(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.event_created_at</code>.
     */
    public LocalDateTime getEventCreatedAt() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>nw.withdrawal_session.event_occured_at</code>.
     */
    public void setEventOccuredAt(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.event_occured_at</code>.
     */
    public LocalDateTime getEventOccuredAt() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>nw.withdrawal_session.sequence_id</code>.
     */
    public void setSequenceId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.sequence_id</code>.
     */
    public Integer getSequenceId() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>nw.withdrawal_session.source_id</code>.
     */
    public void setSourceId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.source_id</code>.
     */
    public String getSourceId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>nw.withdrawal_session.withdrawal_session_id</code>.
     */
    public void setWithdrawalSessionId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.withdrawal_session_id</code>.
     */
    public String getWithdrawalSessionId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>nw.withdrawal_session.withdrawal_session_status</code>.
     */
    public void setWithdrawalSessionStatus(WithdrawalSessionStatus value) {
        set(7, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.withdrawal_session_status</code>.
     */
    public WithdrawalSessionStatus getWithdrawalSessionStatus() {
        return (WithdrawalSessionStatus) get(7);
    }

    /**
     * Setter for <code>nw.withdrawal_session.provider_id</code>.
     */
    public void setProviderId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.provider_id</code>.
     */
    public String getProviderId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>nw.withdrawal_session.withdrawal_id</code>.
     */
    public void setWithdrawalId(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.withdrawal_id</code>.
     */
    public String getWithdrawalId() {
        return (String) get(9);
    }

    /**
     * Setter for <code>nw.withdrawal_session.destination_name</code>.
     */
    public void setDestinationName(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.destination_name</code>.
     */
    public String getDestinationName() {
        return (String) get(10);
    }

    /**
     * Setter for <code>nw.withdrawal_session.destination_card_token</code>.
     */
    public void setDestinationCardToken(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.destination_card_token</code>.
     */
    public String getDestinationCardToken() {
        return (String) get(11);
    }

    /**
     * Setter for <code>nw.withdrawal_session.destination_card_payment_system</code>.
     */
    public void setDestinationCardPaymentSystem(BankCardPaymentSystem value) {
        set(12, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.destination_card_payment_system</code>.
     */
    public BankCardPaymentSystem getDestinationCardPaymentSystem() {
        return (BankCardPaymentSystem) get(12);
    }

    /**
     * Setter for <code>nw.withdrawal_session.destination_card_bin</code>.
     */
    public void setDestinationCardBin(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.destination_card_bin</code>.
     */
    public String getDestinationCardBin() {
        return (String) get(13);
    }

    /**
     * Setter for <code>nw.withdrawal_session.destination_card_masked_pan</code>.
     */
    public void setDestinationCardMaskedPan(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.destination_card_masked_pan</code>.
     */
    public String getDestinationCardMaskedPan() {
        return (String) get(14);
    }

    /**
     * Setter for <code>nw.withdrawal_session.amount</code>.
     */
    public void setAmount(Long value) {
        set(15, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.amount</code>.
     */
    public Long getAmount() {
        return (Long) get(15);
    }

    /**
     * Setter for <code>nw.withdrawal_session.currency_code</code>.
     */
    public void setCurrencyCode(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.currency_code</code>.
     */
    public String getCurrencyCode() {
        return (String) get(16);
    }

    /**
     * Setter for <code>nw.withdrawal_session.sender_party_id</code>.
     */
    public void setSenderPartyId(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.sender_party_id</code>.
     */
    public String getSenderPartyId() {
        return (String) get(17);
    }

    /**
     * Setter for <code>nw.withdrawal_session.sender_provider_id</code>.
     */
    public void setSenderProviderId(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.sender_provider_id</code>.
     */
    public String getSenderProviderId() {
        return (String) get(18);
    }

    /**
     * Setter for <code>nw.withdrawal_session.sender_class_id</code>.
     */
    public void setSenderClassId(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.sender_class_id</code>.
     */
    public String getSenderClassId() {
        return (String) get(19);
    }

    /**
     * Setter for <code>nw.withdrawal_session.sender_contract_id</code>.
     */
    public void setSenderContractId(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.sender_contract_id</code>.
     */
    public String getSenderContractId() {
        return (String) get(20);
    }

    /**
     * Setter for <code>nw.withdrawal_session.receiver_party_id</code>.
     */
    public void setReceiverPartyId(String value) {
        set(21, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.receiver_party_id</code>.
     */
    public String getReceiverPartyId() {
        return (String) get(21);
    }

    /**
     * Setter for <code>nw.withdrawal_session.receiver_provider_id</code>.
     */
    public void setReceiverProviderId(String value) {
        set(22, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.receiver_provider_id</code>.
     */
    public String getReceiverProviderId() {
        return (String) get(22);
    }

    /**
     * Setter for <code>nw.withdrawal_session.receiver_class_id</code>.
     */
    public void setReceiverClassId(String value) {
        set(23, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.receiver_class_id</code>.
     */
    public String getReceiverClassId() {
        return (String) get(23);
    }

    /**
     * Setter for <code>nw.withdrawal_session.receiver_contract_id</code>.
     */
    public void setReceiverContractId(String value) {
        set(24, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.receiver_contract_id</code>.
     */
    public String getReceiverContractId() {
        return (String) get(24);
    }

    /**
     * Setter for <code>nw.withdrawal_session.adapter_state</code>.
     */
    public void setAdapterState(String value) {
        set(25, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.adapter_state</code>.
     */
    public String getAdapterState() {
        return (String) get(25);
    }

    /**
     * Setter for <code>nw.withdrawal_session.tran_info_id</code>.
     */
    public void setTranInfoId(String value) {
        set(26, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.tran_info_id</code>.
     */
    public String getTranInfoId() {
        return (String) get(26);
    }

    /**
     * Setter for <code>nw.withdrawal_session.tran_info_timestamp</code>.
     */
    public void setTranInfoTimestamp(LocalDateTime value) {
        set(27, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.tran_info_timestamp</code>.
     */
    public LocalDateTime getTranInfoTimestamp() {
        return (LocalDateTime) get(27);
    }

    /**
     * Setter for <code>nw.withdrawal_session.tran_info_json</code>.
     */
    public void setTranInfoJson(String value) {
        set(28, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.tran_info_json</code>.
     */
    public String getTranInfoJson() {
        return (String) get(28);
    }

    /**
     * Setter for <code>nw.withdrawal_session.wtime</code>.
     */
    public void setWtime(LocalDateTime value) {
        set(29, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.wtime</code>.
     */
    public LocalDateTime getWtime() {
        return (LocalDateTime) get(29);
    }

    /**
     * Setter for <code>nw.withdrawal_session.current</code>.
     */
    public void setCurrent(Boolean value) {
        set(30, value);
    }

    /**
     * Getter for <code>nw.withdrawal_session.current</code>.
     */
    public Boolean getCurrent() {
        return (Boolean) get(30);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WithdrawalSessionRecord
     */
    public WithdrawalSessionRecord() {
        super(WithdrawalSession.WITHDRAWAL_SESSION);
    }

    /**
     * Create a detached, initialised WithdrawalSessionRecord
     */
    public WithdrawalSessionRecord(Long id, Long eventId, LocalDateTime eventCreatedAt, LocalDateTime eventOccuredAt, Integer sequenceId, String sourceId, String withdrawalSessionId, WithdrawalSessionStatus withdrawalSessionStatus, String providerId, String withdrawalId, String destinationName, String destinationCardToken, BankCardPaymentSystem destinationCardPaymentSystem, String destinationCardBin, String destinationCardMaskedPan, Long amount, String currencyCode, String senderPartyId, String senderProviderId, String senderClassId, String senderContractId, String receiverPartyId, String receiverProviderId, String receiverClassId, String receiverContractId, String adapterState, String tranInfoId, LocalDateTime tranInfoTimestamp, String tranInfoJson, LocalDateTime wtime, Boolean current) {
        super(WithdrawalSession.WITHDRAWAL_SESSION);

        set(0, id);
        set(1, eventId);
        set(2, eventCreatedAt);
        set(3, eventOccuredAt);
        set(4, sequenceId);
        set(5, sourceId);
        set(6, withdrawalSessionId);
        set(7, withdrawalSessionStatus);
        set(8, providerId);
        set(9, withdrawalId);
        set(10, destinationName);
        set(11, destinationCardToken);
        set(12, destinationCardPaymentSystem);
        set(13, destinationCardBin);
        set(14, destinationCardMaskedPan);
        set(15, amount);
        set(16, currencyCode);
        set(17, senderPartyId);
        set(18, senderProviderId);
        set(19, senderClassId);
        set(20, senderContractId);
        set(21, receiverPartyId);
        set(22, receiverProviderId);
        set(23, receiverClassId);
        set(24, receiverContractId);
        set(25, adapterState);
        set(26, tranInfoId);
        set(27, tranInfoTimestamp);
        set(28, tranInfoJson);
        set(29, wtime);
        set(30, current);
    }
}
