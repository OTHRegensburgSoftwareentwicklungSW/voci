package de.majaf.voci.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
public class RegisteredUser extends User implements UserDetails {

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToMany
    private List<RegisteredUser> contacts = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Room> ownedRooms = new ArrayList<>();

    @OneToOne(mappedBy = "initiator")
    private Invitation ownedInvitation;

    @ManyToMany(mappedBy = "invitedUsers")
    private List<Invitation> activeInvitations = new ArrayList<>();

    public RegisteredUser(){ };

    public RegisteredUser(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.passwordHash = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
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

    public void removeActiveInvitation(Invitation invitation) {
        activeInvitations.remove(invitation);
    }

    @Override
    public Boolean isRegistered() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString(){
        return userName + " " + email + " " + passwordHash;
    }
}
