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
public class Invitation extends SingleIdEntity{

    @Column(unique = true)
    private String accessToken;

    @JoinColumn(unique = true, nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private RegisteredUser initiator;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RegisteredUser> invitedUsers = new ArrayList<>();

    @JoinColumn(unique = true)
    @OneToOne(cascade = CascadeType.ALL)
    private Call call;

    public Invitation() {}

    public Invitation(RegisteredUser initiator) {
        this.initiator = initiator;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public RegisteredUser getInitiator() {
        return initiator;
    }

    public void setInitiator(RegisteredUser initiator) {
        this.initiator = initiator;
    }

    public List<RegisteredUser> getInvitedUsers() {
        return Collections.unmodifiableList(invitedUsers);
    }

    public void addInvitedUser(RegisteredUser invitedUser) {
        if(!invitedUsers.contains(invitedUser))
            invitedUsers.add(invitedUser);
    }

    public void removeInvitedUser(RegisteredUser user) {
        invitedUsers.remove(user);
    }

    public void removeAllInvitedUsers() {
        invitedUsers.clear();
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    @Override
    public String toString() {
        return "Invitation from " + initiator + "\nCall is active: " + (call != null) + "\nInvited: " + invitedUsers;
    }
}
