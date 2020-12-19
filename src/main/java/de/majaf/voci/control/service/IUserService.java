package de.majaf.voci.control.service;

import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserAlreadyExistsException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface IUserService extends UserDetailsService {
    void registerUser(RegisteredUser user) throws UserAlreadyExistsException;

    // TODO Exceptions
    User loadUserByID(long id);

    void deleteUserByID(long id);

    RegisteredUser saveUser(RegisteredUser user);

    void addContact(RegisteredUser user, String usernameContact) throws UsernameNotFoundException, InvalidUserException;

    RegisteredUser removeContact(RegisteredUser user, String usernameContact);
}
