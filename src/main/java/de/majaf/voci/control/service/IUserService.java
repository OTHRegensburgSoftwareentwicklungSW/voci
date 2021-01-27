package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.*;
import de.majaf.voci.entity.*;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for managing users
 */
public interface IUserService extends UserDetailsService {

    /**
     * Persists a room in the database
     * @param user user, which should be persisted
     * @return saved user
     */
    User saveUser(User user);


    /**
     * Loads a {@link User} from the database.
     * @param id id from user, which should be loaded
     * @return loaded user
     * @throws UserDoesNotExistException if a user with this id is not in the database
     */
    User loadUserByID(long id) throws UserDoesNotExistException;


    /**
     * Loads a {@link RegisteredUser} from the database.
     * @param securityToken security-token from user, which should be loaded
     * @return loaded user
     * @throws UserTokenDoesNotExistException if a user with this security-token is not in the database
     */
    RegisteredUser loadUserBySecurityToken(String securityToken) throws UserTokenDoesNotExistException;


    /**
     * Creates {@link RegisteredUser} with credentials and persist it in the database.
     * @param user prefilled {@link RegisteredUser}-object with username, password, email
     * @throws UserAlreadyExistsException if a user with the same username or email is already in the database
     * @throws InvalidNameException if the users username or email is empty or to long
     */
    void registerUser(RegisteredUser user) throws UserAlreadyExistsException, InvalidNameException;


    /**
     * Adds another {@link RegisteredUser} to users contacts.
     * @param user user, who gets a new contact
     * @param usernameContact username of future contact
     * @throws UsernameDoesNotExistException if the username of the contact does not match any user in the database
     * @throws InvalidUserException if contact is already in contacts or contact equals user
     * @throws InvalidNameException if usernameContact is empty or too long
     */
    void addContact(RegisteredUser user, String usernameContact) throws UsernameDoesNotExistException, InvalidUserException, InvalidNameException;


    /**
     * Removes a {@link RegisteredUser} from the users contacts.
     * @param user user, who removes a contact
     * @param contactID id from contact, who is to be removed
     * @throws UserDoesNotExistException if the contactID does not match any user in the database
     */
    void removeContact(RegisteredUser user, long contactID) throws UserDoesNotExistException;


    /**
     * Creates a {@link GuestUser} and directly lets him join a call.
     * @param accessToken invitation-token of the invitation, which invites to the call
     * @return the generated guestUser
     * @throws InvitationTokenDoesNotExistException if the accessToken does not match a {@link Invitation} in the database
     * @throws InvalidCallStateException if the Call for this accessToken is not active.
     * @throws InvalidUserException if problems occur when joining the call (does actually not happen)
     */
    GuestUser createGuestUserAndJoinCall(String accessToken) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException;


    /**
     * Updates the users dropsi-security token
     *
     * @param user user, which is to be updated
     * @param token security-token from Dropsi
     * @return updated user-object
     */
    RegisteredUser updateDropsiToken(RegisteredUser user, String token);


    /**
     * Saves in the database, that the user left a {@link VoiceChannel}
     * @param user user, which leaves the VoiceChannel
     */
    void leaveVoiceChannel(User user);
}
