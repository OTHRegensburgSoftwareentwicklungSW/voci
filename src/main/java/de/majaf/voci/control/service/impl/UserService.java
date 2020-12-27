package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserAlreadyExistsException;
import de.majaf.voci.control.service.exceptions.UserIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.UsernameDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import de.majaf.voci.entity.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService implements IUserService {

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
            userRepo.save(user);
            callService.createInvitation(user);
        }
    }

    @Override
    public User loadUserByID(long id) throws UserIDDoesNotExistException {
        Optional<RegisteredUser> u = userRepo.findById(id);
        User user = null;
        if (u.isPresent())
            user = u.get();
        else throw new UserIDDoesNotExistException(id, "Invalid UserID");

        return user;
    }

    @Override
    public void deleteUserByID(long id) {

    }

    @Override
    public User saveUser(User user) {
        if (user.isRegistered())
            return userRepo.save((RegisteredUser) user);

        else return user;
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
        userRepo.save(contact);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void removeContact(RegisteredUser user, long contactID) throws UserIDDoesNotExistException{
        RegisteredUser contact = (RegisteredUser) loadUserByID(contactID);
        contact.removeContact(user);
        user.removeContact(contact);
        callService.removeInvitedUser(user.getOwnedInvitation(), contact);
        userRepo.save(contact);
        userRepo.save(user);
    }

    private boolean userNameAlreadyExist(String username) {
        return userRepo.findByUserName(username).isPresent();
    }

    private boolean emailAlreadyExist(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameDoesNotExistException {
        return userRepo.findByUserName(username).orElseThrow(
                () -> new UsernameDoesNotExistException(username, "User does not exist"));
    }

}
