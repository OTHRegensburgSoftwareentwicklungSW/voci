package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
public class Invitation extends SingleIdEntity{

    private Date creationDate;
    private long timeout;

    @Column(unique = true)
    private String accessToken;

    @JoinColumn(unique = true, nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private RegisteredUser initiator;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RegisteredUser> invitedUsers = new ArrayList<>();

    @JoinColumn(unique = true, nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Call call;

    @OneToMany
    @JoinColumn(name = "invitation_id")
    private List<GuestUser> guestUsers = new ArrayList<>();

    public Invitation() {}

    public Invitation(RegisteredUser initiator, Call call) {
        this.initiator = initiator;
        this.call = call;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
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

    public void setInvitedUsers(List<RegisteredUser> invitedUsers) {
        this.invitedUsers = invitedUsers;
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

    public List<GuestUser> getGuestUsers() {
        return Collections.unmodifiableList(guestUsers);
    }

    public void addGuestUser(GuestUser guestUser) {
        if(!guestUsers.contains(guestUser))
            this.guestUsers.add(guestUser);
    }

    @Override
    public String toString() {
        return "Invitation from " + initiator + "\nCall is active: " + call.isActive() + "\nInvited: " + invitedUsers;
    }
}
