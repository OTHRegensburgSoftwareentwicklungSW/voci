package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.entity.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface IUserService extends UserDetailsService {
    User saveUser(User user);
    User loadUserByID(long id) throws UserDoesNotExistException;

    RegisteredUser loadUserBySecurityToken(String securityToken) throws UserTokenDoesNotExistException;
    void registerUser(RegisteredUser user) throws UserAlreadyExistsException;

    void addContact(RegisteredUser user, String usernameContact) throws UsernameDoesNotExistException, InvalidUserException;
    void removeContact(RegisteredUser user, long contactID) throws UserDoesNotExistException;

    GuestUser createGuestUserAndJoinCall(String accessToken, HttpServletRequest req) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException;
    void removeAllGuests(Call call);
}
