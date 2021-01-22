package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class GuestUser extends User {

    @JsonIgnore
    private String tempName;

    public GuestUser() {}

    public GuestUser(String tempName) {
        this.tempName = tempName;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    @Override
    public String getUserName() {
        return tempName;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return tempName;
    }
}
