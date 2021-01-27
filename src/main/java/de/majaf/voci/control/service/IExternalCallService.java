package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;

/**
 * Service interface for managing external call matters
 */
public interface IExternalCallService {

    /**
     * Creates a {@link Call}-object and adds the user as a participant. It is saved in the database
     * @param user initiator of the call
     * @return generated Call
     */
    Call startCall(RegisteredUser user);


    /**
     * Deletes {@link Call}-object from database and removes references to it.
     * @param call call, which is to be ended
     */
    void endCall(Call call);


    /**
     * Deletes {@link Call}-object from database and removes references to it if provided user is the initiator of the call.
     * @param call call, which is to be ended
     * @param user user, who initiates to end the call.
     * @throws InvalidUserException if the user is not initiator of the call.
     */
    void endCallAuthenticated(Call call, User user) throws InvalidUserException;


    /**
     * Loads invitation from the database that matches the provided invitation-token
     * @param accessToken invitation-access-token belonging to a {@link Invitation}
     * @return loaded invitation
     * @throws InvitationTokenDoesNotExistException if the token does not match any invitation
     */
    Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException;


    /**
     * Loads call from the database having the invitation as attribute that matches invitation-token
     * @param accessToken invitation-access-token belonging to a {@link Invitation}
     * @return loaded call
     * @throws InvitationTokenDoesNotExistException if the token does not match any invitation
     * @throws InvalidCallStateException if invitation with this token has null as call
     */
    Call loadCallByToken(String accessToken) throws InvitationTokenDoesNotExistException, InvalidCallStateException;


    /**
     * Resets invitation-token and call of the provided invitation and saves it in the database
     * @param invitation invitation, which is to be reseted
     */
    void endInvitation(Invitation invitation);
}
