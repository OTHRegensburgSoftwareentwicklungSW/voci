package de.majaf.voci.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class VoiceChannel extends Channel{

    @OneToMany(mappedBy = "activeVoiceChannel")
    private List<User> activeMembers = new ArrayList<>();

    public VoiceChannel() {}

    public VoiceChannel(List<User> activeMembers) {
        this.activeMembers = activeMembers;
    }

    public VoiceChannel(String channelName) {
        super(channelName);
    }

    public List<User> getActiveMembers() {
        return Collections.unmodifiableList(activeMembers);
    }

    public void setActiveMembers(List<User> activeMembers) {
        this.activeMembers = activeMembers;
    }

    public void addActiveMember(User member) {
        if(!activeMembers.contains(member))
            activeMembers.add(member);
    }

    @Override
    public boolean isTextChannel() {
        return false;
    }
}
