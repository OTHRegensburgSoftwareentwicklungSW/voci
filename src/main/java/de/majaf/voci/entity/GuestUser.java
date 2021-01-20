package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import java.util.Collection;

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

    @JsonIgnore
    @Override
    public Boolean isRegistered() {
        return false;
    }

    @JsonIgnore
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
