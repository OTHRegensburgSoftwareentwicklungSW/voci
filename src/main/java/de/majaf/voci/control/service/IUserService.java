package de.majaf.voci.control.service;

import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserAlreadyExistsException;
import de.majaf.voci.control.service.exceptions.UserIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.UsernameDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface IUserService extends UserDetailsService {
    void registerUser(RegisteredUser user) throws UserAlreadyExistsException;

    User loadUserByID(long id) throws UserIDDoesNotExistException;

    void deleteUserByID(long id);

    User saveUser(User user);

    void addContact(RegisteredUser user, String usernameContact) throws UsernameDoesNotExistException, InvalidUserException;

    void removeContact(RegisteredUser user, long contactID) throws UserIDDoesNotExistException;
}
