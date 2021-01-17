package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.entity.GuestUser;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface IUserService extends UserDetailsService {
    void registerUser(RegisteredUser user) throws UserAlreadyExistsException;

    GuestUser createGuestUser(int num);

    void removeAllGuests(Invitation invitation);

    User loadUserByID(long id) throws UserDoesNotExistException;

    RegisteredUser loadUserBySecurityToken(String securityToken) throws UserTokenDoesNotExistException;

    User saveUser(User user);

    void addContact(RegisteredUser user, String usernameContact) throws UsernameDoesNotExistException, InvalidUserException;

    void removeContact(RegisteredUser user, long contactID) throws UserDoesNotExistException;
}
