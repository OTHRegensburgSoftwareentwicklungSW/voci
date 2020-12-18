package de.majaf.voci.entity;

import javax.persistence.*;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class User extends SingleIdEntity{

    @ManyToOne()
    private VoiceChannel activeVoiceChannel;

    @ManyToOne
//    @Transient
    private Call activeCall;

    public VoiceChannel getActiveVoiceChannel() {
        return activeVoiceChannel;
    }

    public Call getActiveCall() {
        return activeCall;
    }
}
