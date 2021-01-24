package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.UserRepository;
import de.majaf.voci.entity.repo.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Scope(value = "singleton")
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
    public GuestUser createGuestUserAndJoinCall(String accessToken, HttpServletRequest req) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException {
        Invitation invitation = callService.loadInvitationByToken(accessToken);
        Call call = invitation.getCall();
        if (call != null) {
            GuestUser guestUser = new GuestUser("guest" + (call.getGuestUsers().size() + 1));
            call.addGuestUser(guestUser);
            callService.joinCall(guestUser, invitation);
            return guestUser;
        } else throw new InvitationTokenDoesNotExistException(accessToken, "No call belongs to this token");
    }

    @Override
    @Transactional
    public void removeAllGuests(Call call) {
        for (GuestUser user : call.getGuestUsers())
            userRepo.delete(user);
    }

    @Override
    public User loadUserByID(long id) throws UserDoesNotExistException {
        return userRepo.findById(id).orElseThrow(() -> new UserDoesNotExistException(id, "Invalid userID"));
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
    public void removeContact(RegisteredUser user, long contactID) throws UserDoesNotExistException {
        RegisteredUser contact = (RegisteredUser) loadUserByID(contactID);
        contact.removeContact(user);
        user.removeContact(contact);
        callService.uninviteUser(user.getOwnedInvitation(), contact);
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
