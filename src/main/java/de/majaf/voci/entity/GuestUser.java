package de.majaf.voci.entity;

// @Entity
public class GuestUser extends User {
    private String tempName;

    @Override
    public Boolean isRegistered() {
        return false;
    }

    @Override
    public String getUserName() {
        return tempName;
    }
}
