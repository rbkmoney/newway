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
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1973685120;

    private Long          id;
    private Long          eventId;
    private LocalDateTime eventCreatedAt;
    private LocalDateTime eventOccuredAt;
    private Integer       sequenceId;
    private String        walletId;
    private String        walletName;
    private String        identityId;
    private String        currencyCode;
    private LocalDateTime wtime;
    private Boolean       current;

    public Wallet() {}

    public Wallet(Wallet value) {
        this.id = value.id;
        this.eventId = value.eventId;
        this.eventCreatedAt = value.eventCreatedAt;
        this.eventOccuredAt = value.eventOccuredAt;
        this.sequenceId = value.sequenceId;
        this.walletId = value.walletId;
        this.walletName = value.walletName;
        this.identityId = value.identityId;
        this.currencyCode = value.currencyCode;
        this.wtime = value.wtime;
        this.current = value.current;
    }

    public Wallet(
        Long          id,
        Long          eventId,
        LocalDateTime eventCreatedAt,
        LocalDateTime eventOccuredAt,
        Integer       sequenceId,
        String        walletId,
        String        walletName,
        String        identityId,
        String        currencyCode,
        LocalDateTime wtime,
        Boolean       current
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventCreatedAt = eventCreatedAt;
        this.eventOccuredAt = eventOccuredAt;
        this.sequenceId = sequenceId;
        this.walletId = walletId;
        this.walletName = walletName;
        this.identityId = identityId;
        this.currencyCode = currencyCode;
        this.wtime = wtime;
        this.current = current;
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

    public LocalDateTime getEventOccuredAt() {
        return this.eventOccuredAt;
    }

    public void setEventOccuredAt(LocalDateTime eventOccuredAt) {
        this.eventOccuredAt = eventOccuredAt;
    }

    public Integer getSequenceId() {
        return this.sequenceId;
    }

    public void setSequenceId(Integer sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getWalletId() {
        return this.walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletName() {
        return this.walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getIdentityId() {
        return this.identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Wallet other = (Wallet) obj;
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
        if (eventOccuredAt == null) {
            if (other.eventOccuredAt != null)
                return false;
        }
        else if (!eventOccuredAt.equals(other.eventOccuredAt))
            return false;
        if (sequenceId == null) {
            if (other.sequenceId != null)
                return false;
        }
        else if (!sequenceId.equals(other.sequenceId))
            return false;
        if (walletId == null) {
            if (other.walletId != null)
                return false;
        }
        else if (!walletId.equals(other.walletId))
            return false;
        if (walletName == null) {
            if (other.walletName != null)
                return false;
        }
        else if (!walletName.equals(other.walletName))
            return false;
        if (identityId == null) {
            if (other.identityId != null)
                return false;
        }
        else if (!identityId.equals(other.identityId))
            return false;
        if (currencyCode == null) {
            if (other.currencyCode != null)
                return false;
        }
        else if (!currencyCode.equals(other.currencyCode))
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
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.eventId == null) ? 0 : this.eventId.hashCode());
        result = prime * result + ((this.eventCreatedAt == null) ? 0 : this.eventCreatedAt.hashCode());
        result = prime * result + ((this.eventOccuredAt == null) ? 0 : this.eventOccuredAt.hashCode());
        result = prime * result + ((this.sequenceId == null) ? 0 : this.sequenceId.hashCode());
        result = prime * result + ((this.walletId == null) ? 0 : this.walletId.hashCode());
        result = prime * result + ((this.walletName == null) ? 0 : this.walletName.hashCode());
        result = prime * result + ((this.identityId == null) ? 0 : this.identityId.hashCode());
        result = prime * result + ((this.currencyCode == null) ? 0 : this.currencyCode.hashCode());
        result = prime * result + ((this.wtime == null) ? 0 : this.wtime.hashCode());
        result = prime * result + ((this.current == null) ? 0 : this.current.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Wallet (");

        sb.append(id);
        sb.append(", ").append(eventId);
        sb.append(", ").append(eventCreatedAt);
        sb.append(", ").append(eventOccuredAt);
        sb.append(", ").append(sequenceId);
        sb.append(", ").append(walletId);
        sb.append(", ").append(walletName);
        sb.append(", ").append(identityId);
        sb.append(", ").append(currencyCode);
        sb.append(", ").append(wtime);
        sb.append(", ").append(current);

        sb.append(")");
        return sb.toString();
    }
}
