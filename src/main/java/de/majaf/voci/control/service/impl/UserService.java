package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.entity.GuestUser;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import de.majaf.voci.entity.repo.UserRepository;
import de.majaf.voci.entity.repo.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    private RegisteredUserRepository regUserRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ICallService callService;

    @Override
    public void registerUser(RegisteredUser user) throws UserAlreadyExistsException {
        String username = user.getUserName();
        String email = user.getEmail();
        if (userNameAlreadyExist(username))
            throw new UserAlreadyExistsException(user, "User with username " + username + " already exists");
        else if (emailAlreadyExist(email))
            throw new UserAlreadyExistsException(user, "User with email " + email + " already exists");
        else {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            user.setSecurityToken(UUID.randomUUID().toString());
            regUserRepo.save(user);
            callService.createInvitation(user);
        }
    }

    @Override
    @Transactional
    public GuestUser createGuestUser(int num) {
        GuestUser guestUser = new GuestUser("guest" + num);
        userRepo.save(guestUser);
        return guestUser;
    }

    @Override
    public void removeAllGuests(Invitation invitation) {
        for(GuestUser user : invitation.getGuestUsers())
            userRepo.delete(user);
    }

    @Override
    public User loadUserByID(long id) throws UserIDDoesNotExistException {
        return userRepo.findById(id).orElseThrow(() -> new UserIDDoesNotExistException(id, "Invalid userID"));
    }

    @Override
    public RegisteredUser loadUserBySecurityToken(String securityToken) throws UserTokenDoesNotExistException {
        return regUserRepo.findBySecurityToken(securityToken).orElseThrow(() -> new UserTokenDoesNotExistException(securityToken, "Invalid UserToken"));
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public void addContact(RegisteredUser user, String usernameContact) throws UsernameDoesNotExistException, InvalidUserException {
        RegisteredUser contact = (RegisteredUser) loadUserByUsername(usernameContact);
        if (user.getContacts().contains(contact) || contact.getContacts().contains(user))
            throw new InvalidUserException(contact, contact.getUserName() + " is already in contacts!");
        if (user.equals(contact))
            throw new InvalidUserException(contact, "Cannot add yourself as contact!");
        contact.addContact(user);
        user.addContact(contact);
        regUserRepo.save(contact);
        regUserRepo.save(user);
    }

    @Override
    @Transactional
    public void removeContact(RegisteredUser user, long contactID) throws UserIDDoesNotExistException {
        RegisteredUser contact = (RegisteredUser) loadUserByID(contactID);
        contact.removeContact(user);
        user.removeContact(contact);
        callService.removeInvitedUser(user.getOwnedInvitation(), contact);
        regUserRepo.save(contact);
        regUserRepo.save(user);
    }

    private boolean userNameAlreadyExist(String username) {
        return regUserRepo.findByUserName(username).isPresent();
    }

    private boolean emailAlreadyExist(String email) {
        return regUserRepo.findByEmail(email).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameDoesNotExistException {
        return regUserRepo.findByUserName(username).orElseThrow(
                () -> new UsernameDoesNotExistException(username, "User does not exist"));
    }

}
