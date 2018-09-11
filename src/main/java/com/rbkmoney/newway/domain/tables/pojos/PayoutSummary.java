/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.newway.domain.tables.pojos;


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
public class PayoutSummary implements Serializable {

    private static final long serialVersionUID = 1278312241;

    private Long          id;
    private Long          pytId;
    private Long          amount;
    private Long          fee;
    private String        currencyCode;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private String        operationType;
    private Integer       count;

    public PayoutSummary() {}

    public PayoutSummary(PayoutSummary value) {
        this.id = value.id;
        this.pytId = value.pytId;
        this.amount = value.amount;
        this.fee = value.fee;
        this.currencyCode = value.currencyCode;
        this.fromTime = value.fromTime;
        this.toTime = value.toTime;
        this.operationType = value.operationType;
        this.count = value.count;
    }

    public PayoutSummary(
        Long          id,
        Long          pytId,
        Long          amount,
        Long          fee,
        String        currencyCode,
        LocalDateTime fromTime,
        LocalDateTime toTime,
        String        operationType,
        Integer       count
    ) {
        this.id = id;
        this.pytId = pytId;
        this.amount = amount;
        this.fee = fee;
        this.currencyCode = currencyCode;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.operationType = operationType;
        this.count = count;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPytId() {
        return this.pytId;
    }

    public void setPytId(Long pytId) {
        this.pytId = pytId;
    }

    public Long getAmount() {
        return this.amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getFee() {
        return this.fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public LocalDateTime getFromTime() {
        return this.fromTime;
    }

    public void setFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalDateTime getToTime() {
        return this.toTime;
    }

    public void setToTime(LocalDateTime toTime) {
        this.toTime = toTime;
    }

    public String getOperationType() {
        return this.operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PayoutSummary other = (PayoutSummary) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (pytId == null) {
            if (other.pytId != null)
                return false;
        }
        else if (!pytId.equals(other.pytId))
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        }
        else if (!amount.equals(other.amount))
            return false;
        if (fee == null) {
            if (other.fee != null)
                return false;
        }
        else if (!fee.equals(other.fee))
            return false;
        if (currencyCode == null) {
            if (other.currencyCode != null)
                return false;
        }
        else if (!currencyCode.equals(other.currencyCode))
            return false;
        if (fromTime == null) {
            if (other.fromTime != null)
                return false;
        }
        else if (!fromTime.equals(other.fromTime))
            return false;
        if (toTime == null) {
            if (other.toTime != null)
                return false;
        }
        else if (!toTime.equals(other.toTime))
            return false;
        if (operationType == null) {
            if (other.operationType != null)
                return false;
        }
        else if (!operationType.equals(other.operationType))
            return false;
        if (count == null) {
            if (other.count != null)
                return false;
        }
        else if (!count.equals(other.count))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.pytId == null) ? 0 : this.pytId.hashCode());
        result = prime * result + ((this.amount == null) ? 0 : this.amount.hashCode());
        result = prime * result + ((this.fee == null) ? 0 : this.fee.hashCode());
        result = prime * result + ((this.currencyCode == null) ? 0 : this.currencyCode.hashCode());
        result = prime * result + ((this.fromTime == null) ? 0 : this.fromTime.hashCode());
        result = prime * result + ((this.toTime == null) ? 0 : this.toTime.hashCode());
        result = prime * result + ((this.operationType == null) ? 0 : this.operationType.hashCode());
        result = prime * result + ((this.count == null) ? 0 : this.count.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PayoutSummary (");

        sb.append(id);
        sb.append(", ").append(pytId);
        sb.append(", ").append(amount);
        sb.append(", ").append(fee);
        sb.append(", ").append(currencyCode);
        sb.append(", ").append(fromTime);
        sb.append(", ").append(toTime);
        sb.append(", ").append(operationType);
        sb.append(", ").append(count);

        sb.append(")");
        return sb.toString();
    }
}
