package de.majaf.voci.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Room extends SingleIdEntity{

    @Column(nullable = false)
    private String roomName;

    //@Column(nullable = false)
    @OneToMany
    private List<VoiceChannel> voiceChannels = new ArrayList<>();

    //@Column(nullable = false)
    @OneToMany
    private List<VoiceChannel> textChannels = new ArrayList<>();

    @Column(nullable = false)
    @ManyToMany
    private List<RegisteredUser> members = new ArrayList<>();

    @ManyToOne
    private RegisteredUser owner;

    public Room() {}

    public Room(String roomName, RegisteredUser owner) {
        this.roomName = roomName;
        this.owner = owner;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<VoiceChannel> getVoiceChannels() {
        return voiceChannels;
    }

    public void setVoiceChannels(List<VoiceChannel> voiceChannels) {
        this.voiceChannels = voiceChannels;
    }

    public List<VoiceChannel> getTextChannels() {
        return textChannels;
    }

    public void setTextChannels(List<VoiceChannel> textChannels) {
        this.textChannels = textChannels;
    }

    public List<RegisteredUser> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void addMember(RegisteredUser member) {
        if(!members.contains(member))
            members.add(member);
    }

    public void removeMember(RegisteredUser member) {
        members.remove(member);
    }

    public RegisteredUser getOwner() {
        return owner;
    }

    public void setOwner(RegisteredUser owner) {
        this.owner = owner;
    }
}
