package de.majaf.voci.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity

// rename table because call is taken by MYSQL
@Table(name = "communication")
public class Call extends SingleIdEntity {

    @OneToMany(mappedBy = "activeCall")
    private List<User> participants = new ArrayList<>();

    @OneToOne(mappedBy = "call")
    private Invitation invitation;

    @OneToOne
    private VoiceChannel voiceChannel;

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

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void addParticipant(User participant) {
        if (!participants.contains(participant))
            participants.add(participant);
    }

    public void addParticipants(List<User> participants) {
        for (User u : participants)
            addParticipant(u);
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

