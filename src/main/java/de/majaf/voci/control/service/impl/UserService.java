package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.control.service.utils.ServiceUtils;
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
import java.util.logging.Logger;

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

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private Logger logger;

    @Override
    public void registerUser(RegisteredUser user) throws UserAlreadyExistsException, InvalidNameException {
        if(!serviceUtils.checkName(user.getUserName())) throw new InvalidNameException(user.getUserName(), "Username is not valid");
        if(!serviceUtils.checkName(user.getEmail())) throw new InvalidNameException(user.getEmail(), "Email is not valid");

        String username = user.getUserName().trim();
        String email = user.getEmail().trim();

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
        logger.info("Created a new user: " + user.getUserName());
    }

    @Override
    public GuestUser createGuestUserAndJoinCall(String accessToken) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException {
        Invitation invitation = callService.loadInvitationByToken(accessToken);
        Call call = invitation.getCall();
        if (call != null) {
            GuestUser guestUser = new GuestUser("guest" + (call.getGuestUsers().size() + 1));
            call.addGuestUser(guestUser);
            callService.joinCall(guestUser, invitation);
            logger.info("Created a new guest-user " + guestUser.getUserName() + ", invted by " + invitation.getInitiator().getUserName());
            return guestUser;
        } else throw new InvitationTokenDoesNotExistException(accessToken, "No call belongs to this token");
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
    public void addContact(RegisteredUser user, String usernameContact) throws UsernameDoesNotExistException, InvalidUserException, InvalidNameException {
        if(!serviceUtils.checkName(usernameContact))
            throw new InvalidNameException(usernameContact, "Invalid Username");
        RegisteredUser contact = (RegisteredUser) loadUserByUsername(usernameContact.trim());
        if (user.getContacts().contains(contact) || contact.getContacts().contains(user))
            throw new InvalidUserException(contact, contact.getUserName() + " is already in contacts!");
        if (user.equals(contact))
            throw new InvalidUserException(contact, "Cannot add yourself as contact!");
        contact.addContact(user);
        user.addContact(contact);
        regUserRepo.save(contact);
        regUserRepo.save(user);
        logger.info(user.getUserName() + " added " + contact.getUserName() + " as a new contact.");
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
        logger.info(user.getUserName()  + " removed " + contact.getUserName() + " from contact list.");
    }

    private boolean userNameAlreadyExist(String username) {
        return regUserRepo.findByUserName(username).isPresent();
    }

    private boolean emailAlreadyExist(String email) {
        return regUserRepo.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameDoesNotExistException {
        return regUserRepo.findByUserName(username).orElseThrow(
                () -> new UsernameDoesNotExistException(username, "User does not exist"));
    }

    @Override
    public RegisteredUser updateDropsiToken(RegisteredUser user, String token) {
        if (token == null || token.equals(""))
            user.setDropsiToken(null);
        else user.setDropsiToken(token);
        userRepo.save(user);
        logger.info(user.getUserName() + " updated his/her Dropsi-token");
        return user;
    }

    @Override
    @Transactional
    public void leaveVoiceChannel(User user) {
        user.setActiveVoiceChannel(null);
        logger.info(user.getUserName() + " left a voice-channel (if he/she was in one).");
        userRepo.save(user);
    }
}
