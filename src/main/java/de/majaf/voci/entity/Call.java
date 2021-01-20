package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity

// rename table because call is taken by MYSQL
@Table(name = "communication")
public class Call extends SingleIdEntity {

    @OneToMany(mappedBy = "activeCall", fetch = FetchType.EAGER)
    private List<User> participants = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "call")
    private Invitation invitation;

    @JsonIgnore
    @OneToOne
    private VoiceChannel voiceChannel;

    @JsonIgnore
    @OneToOne
    private TextChannel textChannel;

    private Boolean active = false;

    public Call() {
    }

    public Call(Invitation invitation) {
        this.invitation = invitation;
    }

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        addParticipant(invitation.getInitiator());
        this.invitation = invitation;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public List<User> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public void addParticipant(User participant) {
        if (!participants.contains(participant))
            participants.add(participant);
    }

    public void removeParticipant(User participant) {
        participants.remove(participant);
    }

    public void removeAllParticipants() {
        participants.clear();
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

