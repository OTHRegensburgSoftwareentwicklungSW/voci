package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Collection;

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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_GUEST");
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
