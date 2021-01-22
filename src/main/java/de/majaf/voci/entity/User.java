package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegisteredUser.class, name = "registeredUser"),
        @JsonSubTypes.Type(value = GuestUser.class, name = "guestUser")})
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class User extends SingleIdEntity implements UserDetails {

    @JsonIgnore
    @ManyToOne
    private VoiceChannel activeVoiceChannel;

    @JsonIgnore
    @ManyToOne
    private Call activeCall;

    public VoiceChannel getActiveVoiceChannel() {
        return activeVoiceChannel;
    }

    public Call getActiveCall() {
        return activeCall;
    }

    public void setActiveVoiceChannel(VoiceChannel activeVoiceChannel) {
        this.activeVoiceChannel = activeVoiceChannel;
    }

    public void setActiveCall(Call activeCall) {
        this.activeCall = activeCall;
    }

    // TODO: Only for thymeleaf, because there is no other real workaround
    @JsonIgnore
    public Boolean isRegistered() {
        return this instanceof RegisteredUser;
    };

    public boolean isInCall() {
        return activeCall != null;
    }

    public abstract String getUserName();

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @Override
    public abstract String getPassword();

    @JsonIgnore
    @Override
    public abstract String getUsername();
}
