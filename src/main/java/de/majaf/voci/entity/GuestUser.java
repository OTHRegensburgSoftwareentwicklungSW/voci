package de.majaf.voci.entity;

import javax.persistence.Entity;

@Entity
public class GuestUser extends User {
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
    public Boolean isRegistered() {
        return false;
    }

    @Override
    public String getUserName() {
        return tempName;
    }
}
