package de.majaf.voci.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class RegisteredUser extends User {

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @JsonIgnore
    @Column(unique = true)
    private String securityToken;

    @JsonIgnore
    @Column(unique = true)
    private String dropsiToken;

    @JsonIgnore
    @ManyToMany
    private List<RegisteredUser> contacts = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private List<Room> rooms = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Room> ownedRooms = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "initiator")
    private Invitation ownedInvitation;

    @JsonIgnore
    @ManyToMany(mappedBy = "invitedUsers")
    private List<Invitation> activeInvitations = new ArrayList<>();

    public RegisteredUser(){ }

    public RegisteredUser(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.passwordHash = password;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = password;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public List<RegisteredUser> getContacts() {
        return Collections.unmodifiableList(contacts);
    }

    public void addContact(RegisteredUser contact) {
        if(!contacts.contains(contact))
            contacts.add(contact);
    }

    public void removeContact(RegisteredUser contact) {
        contacts.remove(contact);
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public void addRoom(Room room) {
        if(!rooms.contains(room))
            rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public List<Room> getOwnedRooms() {
        return Collections.unmodifiableList(ownedRooms);
    }

    public void addOwnedRoom(Room room) {
        if(!ownedRooms.contains(room))
            ownedRooms.add(room);
    }

    public void removeOwnedRoom(Room room) {
        ownedRooms.remove(room);
    }

    public Invitation getOwnedInvitation() {
        return ownedInvitation;
    }

    public void setOwnedInvitation(Invitation ownedInvitation) {
        this.ownedInvitation = ownedInvitation;
    }

    public List<Invitation> getActiveInvitations() {
        return Collections.unmodifiableList(activeInvitations);
    }

    public void addActiveInvitation(Invitation invitation) {
        if(!activeInvitations.contains(invitation))
            activeInvitations.add(invitation);
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getDropsiToken() {
        return dropsiToken;
    }

    public void setDropsiToken(String dropsiToken) {
        this.dropsiToken = dropsiToken;
    }

    public void removeActiveInvitation(Invitation invitation) {
        activeInvitations.remove(invitation);
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String toString(){
        return userName + " " + email + " " + passwordHash;
    }
}
