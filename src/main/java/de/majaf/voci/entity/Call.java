package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")

// rename table because call is taken by MYSQL
@Table(name = "communication")
public class Call extends SingleIdEntity {

    private long timeout = 60; // timeout in minutes

    private Date creationDate;

    @OneToMany(mappedBy = "activeCall", fetch = FetchType.EAGER)
    private List<User> participants = new ArrayList<>();

    @OneToOne(mappedBy = "call")
    private Invitation invitation;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "call_id")
    private List<GuestUser> guestUsers = new ArrayList<>();

    @JsonIgnore
    @OneToOne(orphanRemoval = true)
    private VoiceChannel voiceChannel;

    @JsonIgnore
    @OneToOne(orphanRemoval = true)
    private TextChannel textChannel;

    public Call() {
    }

    public Call(Invitation invitation) {
        this.invitation = invitation;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    public List<GuestUser> getGuestUsers() {
        return Collections.unmodifiableList(guestUsers);
    }

    public void addGuestUser(GuestUser guestUser) {
        if(!guestUsers.contains(guestUser))
            this.guestUsers.add(guestUser);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}

