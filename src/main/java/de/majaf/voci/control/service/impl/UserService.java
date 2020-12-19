package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserAlreadyExistsException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import de.majaf.voci.entity.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public void registerUser(RegisteredUser user) throws UserAlreadyExistsException {
        String username = user.getUserName();
        String email = user.getEmail();
        if (userNameAlreadyExist(username))
            throw new UserAlreadyExistsException("User with username " + username + " already exists");
        else if(emailAlreadyExist(email))
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        else{
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            userRepo.save(user);
        }
    }

    @Override
    public User loadUserByID(long id) {
        Optional<RegisteredUser> u = userRepo.findById((int) id);
        User user = null;
        if (u.isPresent())
            user = u.get();

        return user;
    }

    @Override
    public void deleteUserByID(long id) {

    }

    @Override
    public RegisteredUser saveUser(RegisteredUser user) {
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public void addContact(RegisteredUser user, String usernameContact) throws UsernameNotFoundException, InvalidUserException{
        RegisteredUser contact = (RegisteredUser) loadUserByUsername(usernameContact);
        if(user.getContacts().contains(contact) || contact.getContacts().contains(user))
            throw new UserAlreadyExistsException(contact.getUserName() + " is already in contacts!");
        if(user.equals(contact))
            throw new InvalidUserException("Cannot add yourself as contact!");
        contact.addContact(user);
        user.addContact(contact);
        userRepo.save(contact);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public RegisteredUser removeContact(RegisteredUser user, String usernameContact) {
        RegisteredUser contact = (RegisteredUser) loadUserByUsername(usernameContact);
        contact.removeContact(user);
        user.removeContact(contact);
        userRepo.save(contact);
        userRepo.save(user);
        return contact;
    }

    private boolean userNameAlreadyExist(String username) {
        return userRepo.findByUserName(username).isPresent();
    }

    private boolean emailAlreadyExist(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUserName(username).orElseThrow(
                () -> new UsernameNotFoundException("User with name " + username + " does not exist"));
    }

}
