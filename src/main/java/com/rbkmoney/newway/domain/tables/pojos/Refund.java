/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables.pojos;


import com.rbkmoney.newway.domain.enums.RefundStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Generated;


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
public class Refund implements Serializable {

    private static final long serialVersionUID = -1915405025;

    private Long          id;
    private Long          eventId;
    private LocalDateTime eventCreatedAt;
    private Long          domainRevision;
    private String        refundId;
    private String        paymentId;
    private String        invoiceId;
    private String        partyId;
    private String        shopId;
    private LocalDateTime createdAt;
    private RefundStatus  status;
    private String        statusFailedFailure;
    private Long          amount;
    private String        currencyCode;
    private String        reason;
    private LocalDateTime wtime;
    private Boolean       current;
    private String        sessionPayloadTransactionBoundTrxId;
    private String        sessionPayloadTransactionBoundTrxExtraJson;
    private Long          fee;
    private Long          providerFee;
    private Long          externalFee;
    private Long          partyRevision;

    public Refund() {}

    public Refund(Refund value) {
        this.id = value.id;
        this.eventId = value.eventId;
        this.eventCreatedAt = value.eventCreatedAt;
        this.domainRevision = value.domainRevision;
        this.refundId = value.refundId;
        this.paymentId = value.paymentId;
        this.invoiceId = value.invoiceId;
        this.partyId = value.partyId;
        this.shopId = value.shopId;
        this.createdAt = value.createdAt;
        this.status = value.status;
        this.statusFailedFailure = value.statusFailedFailure;
        this.amount = value.amount;
        this.currencyCode = value.currencyCode;
        this.reason = value.reason;
        this.wtime = value.wtime;
        this.current = value.current;
        this.sessionPayloadTransactionBoundTrxId = value.sessionPayloadTransactionBoundTrxId;
        this.sessionPayloadTransactionBoundTrxExtraJson = value.sessionPayloadTransactionBoundTrxExtraJson;
        this.fee = value.fee;
        this.providerFee = value.providerFee;
        this.externalFee = value.externalFee;
        this.partyRevision = value.partyRevision;
    }

    public Refund(
        Long          id,
        Long          eventId,
        LocalDateTime eventCreatedAt,
        Long          domainRevision,
        String        refundId,
        String        paymentId,
        String        invoiceId,
        String        partyId,
        String        shopId,
        LocalDateTime createdAt,
        RefundStatus  status,
        String        statusFailedFailure,
        Long          amount,
        String        currencyCode,
        String        reason,
        LocalDateTime wtime,
        Boolean       current,
        String        sessionPayloadTransactionBoundTrxId,
        String        sessionPayloadTransactionBoundTrxExtraJson,
        Long          fee,
        Long          providerFee,
        Long          externalFee,
        Long          partyRevision
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventCreatedAt = eventCreatedAt;
        this.domainRevision = domainRevision;
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.partyId = partyId;
        this.shopId = shopId;
        this.createdAt = createdAt;
        this.status = status;
        this.statusFailedFailure = statusFailedFailure;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.reason = reason;
        this.wtime = wtime;
        this.current = current;
        this.sessionPayloadTransactionBoundTrxId = sessionPayloadTransactionBoundTrxId;
        this.sessionPayloadTransactionBoundTrxExtraJson = sessionPayloadTransactionBoundTrxExtraJson;
        this.fee = fee;
        this.providerFee = providerFee;
        this.externalFee = externalFee;
        this.partyRevision = partyRevision;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getEventCreatedAt() {
        return this.eventCreatedAt;
    }

    public void setEventCreatedAt(LocalDateTime eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
    }

    public Long getDomainRevision() {
        return this.domainRevision;
    }

    public void setDomainRevision(Long domainRevision) {
        this.domainRevision = domainRevision;
    }

    public String getRefundId() {
        return this.refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public RefundStatus getStatus() {
        return this.status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public String getStatusFailedFailure() {
        return this.statusFailedFailure;
    }

    public void setStatusFailedFailure(String statusFailedFailure) {
        this.statusFailedFailure = statusFailedFailure;
    }

    public Long getAmount() {
        return this.amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getWtime() {
        return this.wtime;
    }

    public void setWtime(LocalDateTime wtime) {
        this.wtime = wtime;
    }

    public Boolean getCurrent() {
        return this.current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public String getSessionPayloadTransactionBoundTrxId() {
        return this.sessionPayloadTransactionBoundTrxId;
    }

    public void setSessionPayloadTransactionBoundTrxId(String sessionPayloadTransactionBoundTrxId) {
        this.sessionPayloadTransactionBoundTrxId = sessionPayloadTransactionBoundTrxId;
    }

    public String getSessionPayloadTransactionBoundTrxExtraJson() {
        return this.sessionPayloadTransactionBoundTrxExtraJson;
    }

    public void setSessionPayloadTransactionBoundTrxExtraJson(String sessionPayloadTransactionBoundTrxExtraJson) {
        this.sessionPayloadTransactionBoundTrxExtraJson = sessionPayloadTransactionBoundTrxExtraJson;
    }

    public Long getFee() {
        return this.fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Long getProviderFee() {
        return this.providerFee;
    }

    public void setProviderFee(Long providerFee) {
        this.providerFee = providerFee;
    }

    public Long getExternalFee() {
        return this.externalFee;
    }

    public void setExternalFee(Long externalFee) {
        this.externalFee = externalFee;
    }

    public Long getPartyRevision() {
        return this.partyRevision;
    }

    public void setPartyRevision(Long partyRevision) {
        this.partyRevision = partyRevision;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Refund other = (Refund) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (eventId == null) {
            if (other.eventId != null)
                return false;
        }
        else if (!eventId.equals(other.eventId))
            return false;
        if (eventCreatedAt == null) {
            if (other.eventCreatedAt != null)
                return false;
        }
        else if (!eventCreatedAt.equals(other.eventCreatedAt))
            return false;
        if (domainRevision == null) {
            if (other.domainRevision != null)
                return false;
        }
        else if (!domainRevision.equals(other.domainRevision))
            return false;
        if (refundId == null) {
            if (other.refundId != null)
                return false;
        }
        else if (!refundId.equals(other.refundId))
            return false;
        if (paymentId == null) {
            if (other.paymentId != null)
                return false;
        }
        else if (!paymentId.equals(other.paymentId))
            return false;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        }
        else if (!invoiceId.equals(other.invoiceId))
            return false;
        if (partyId == null) {
            if (other.partyId != null)
                return false;
        }
        else if (!partyId.equals(other.partyId))
            return false;
        if (shopId == null) {
            if (other.shopId != null)
                return false;
        }
        else if (!shopId.equals(other.shopId))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        }
        else if (!createdAt.equals(other.createdAt))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        }
        else if (!status.equals(other.status))
            return false;
        if (statusFailedFailure == null) {
            if (other.statusFailedFailure != null)
                return false;
        }
        else if (!statusFailedFailure.equals(other.statusFailedFailure))
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        }
        else if (!amount.equals(other.amount))
            return false;
        if (currencyCode == null) {
            if (other.currencyCode != null)
                return false;
        }
        else if (!currencyCode.equals(other.currencyCode))
            return false;
        if (reason == null) {
            if (other.reason != null)
                return false;
        }
        else if (!reason.equals(other.reason))
            return false;
        if (wtime == null) {
            if (other.wtime != null)
                return false;
        }
        else if (!wtime.equals(other.wtime))
            return false;
        if (current == null) {
            if (other.current != null)
                return false;
        }
        else if (!current.equals(other.current))
            return false;
        if (sessionPayloadTransactionBoundTrxId == null) {
            if (other.sessionPayloadTransactionBoundTrxId != null)
                return false;
        }
        else if (!sessionPayloadTransactionBoundTrxId.equals(other.sessionPayloadTransactionBoundTrxId))
            return false;
        if (sessionPayloadTransactionBoundTrxExtraJson == null) {
            if (other.sessionPayloadTransactionBoundTrxExtraJson != null)
                return false;
        }
        else if (!sessionPayloadTransactionBoundTrxExtraJson.equals(other.sessionPayloadTransactionBoundTrxExtraJson))
            return false;
        if (fee == null) {
            if (other.fee != null)
                return false;
        }
        else if (!fee.equals(other.fee))
            return false;
        if (providerFee == null) {
            if (other.providerFee != null)
                return false;
        }
        else if (!providerFee.equals(other.providerFee))
            return false;
        if (externalFee == null) {
            if (other.externalFee != null)
                return false;
        }
        else if (!externalFee.equals(other.externalFee))
            return false;
        if (partyRevision == null) {
            if (other.partyRevision != null)
                return false;
        }
        else if (!partyRevision.equals(other.partyRevision))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.eventId == null) ? 0 : this.eventId.hashCode());
        result = prime * result + ((this.eventCreatedAt == null) ? 0 : this.eventCreatedAt.hashCode());
        result = prime * result + ((this.domainRevision == null) ? 0 : this.domainRevision.hashCode());
        result = prime * result + ((this.refundId == null) ? 0 : this.refundId.hashCode());
        result = prime * result + ((this.paymentId == null) ? 0 : this.paymentId.hashCode());
        result = prime * result + ((this.invoiceId == null) ? 0 : this.invoiceId.hashCode());
        result = prime * result + ((this.partyId == null) ? 0 : this.partyId.hashCode());
        result = prime * result + ((this.shopId == null) ? 0 : this.shopId.hashCode());
        result = prime * result + ((this.createdAt == null) ? 0 : this.createdAt.hashCode());
        result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
        result = prime * result + ((this.statusFailedFailure == null) ? 0 : this.statusFailedFailure.hashCode());
        result = prime * result + ((this.amount == null) ? 0 : this.amount.hashCode());
        result = prime * result + ((this.currencyCode == null) ? 0 : this.currencyCode.hashCode());
        result = prime * result + ((this.reason == null) ? 0 : this.reason.hashCode());
        result = prime * result + ((this.wtime == null) ? 0 : this.wtime.hashCode());
        result = prime * result + ((this.current == null) ? 0 : this.current.hashCode());
        result = prime * result + ((this.sessionPayloadTransactionBoundTrxId == null) ? 0 : this.sessionPayloadTransactionBoundTrxId.hashCode());
        result = prime * result + ((this.sessionPayloadTransactionBoundTrxExtraJson == null) ? 0 : this.sessionPayloadTransactionBoundTrxExtraJson.hashCode());
        result = prime * result + ((this.fee == null) ? 0 : this.fee.hashCode());
        result = prime * result + ((this.providerFee == null) ? 0 : this.providerFee.hashCode());
        result = prime * result + ((this.externalFee == null) ? 0 : this.externalFee.hashCode());
        result = prime * result + ((this.partyRevision == null) ? 0 : this.partyRevision.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Refund (");

        sb.append(id);
        sb.append(", ").append(eventId);
        sb.append(", ").append(eventCreatedAt);
        sb.append(", ").append(domainRevision);
        sb.append(", ").append(refundId);
        sb.append(", ").append(paymentId);
        sb.append(", ").append(invoiceId);
        sb.append(", ").append(partyId);
        sb.append(", ").append(shopId);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(status);
        sb.append(", ").append(statusFailedFailure);
        sb.append(", ").append(amount);
        sb.append(", ").append(currencyCode);
        sb.append(", ").append(reason);
        sb.append(", ").append(wtime);
        sb.append(", ").append(current);
        sb.append(", ").append(sessionPayloadTransactionBoundTrxId);
        sb.append(", ").append(sessionPayloadTransactionBoundTrxExtraJson);
        sb.append(", ").append(fee);
        sb.append(", ").append(providerFee);
        sb.append(", ").append(externalFee);
        sb.append(", ").append(partyRevision);

        sb.append(")");
        return sb.toString();
    }
}
