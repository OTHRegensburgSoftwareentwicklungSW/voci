package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class User extends SingleIdEntity{

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

    public abstract Boolean isRegistered();

    public boolean isInCall() {
        return activeCall != null;
    }

    public abstract String getUserName();

}
