package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.entity.*;
import de.majaf.voci.entity.repo.UserRepository;
import de.majaf.voci.entity.repo.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

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
    public GuestUser createGuestUser(String accessToken, HttpServletRequest req) throws InvitationTokenDoesNotExistException {
        Invitation invitation = callService.loadInvitationByToken(accessToken);
        GuestUser guestUser = new GuestUser("guest" + (invitation.getGuestUsers().size() + 1));
        authenticateUser(guestUser, req);
        guestUser.setActiveCall(invitation.getCall());
        guestUser = userRepo.save(guestUser);
        invitation.getCall().addParticipant(guestUser);
        invitation.addGuestUser(guestUser);
        callService.saveInvitation(invitation);
        return guestUser;
    }

    @Override
    @Transactional
    public void removeAllGuests(Invitation invitation) {
        for(GuestUser user : invitation.getGuestUsers())
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
    public void deleteUser(User user) {
        userRepo.delete(user);
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

    @Override
    public RegisteredUser authenticateUser(String securityToken, HttpServletRequest req) throws UserTokenDoesNotExistException {
        RegisteredUser user = loadUserBySecurityToken(securityToken);
        authenticateUser(user, req);
        return user;
    }

    private void authenticateUser(User user, HttpServletRequest req) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, ((UserDetails) user).getAuthorities());
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
    }

}
