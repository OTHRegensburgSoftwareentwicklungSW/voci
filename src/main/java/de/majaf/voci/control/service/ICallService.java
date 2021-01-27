package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.*;

import java.util.List;

/**
 * Service interface for managing internal call matters
 */
public interface ICallService extends IExternalCallService{

    /**
     * Loads all calls from database
     *
     * @return list of calls
     */
    List<Call> getAllCalls();


    /**
     * Creates invitation for a {@link RegisteredUser} and saves it in database
     *
     * @param initiator future owner of the invitation
     * @return created invitation
     */
    Invitation createInvitation(RegisteredUser initiator);


    /**
     * Adds a contact to invited users in invitation.
     *
     * @param invitation invitation, which is to be updated
     * @param invitedContactID id of invited contact
     * @throws UserDoesNotExistException if a contact with this id does not exist
     * @throws InvalidUserException if the user with this id is not in contacts of invitation-owner
     */
    void inviteToCall(Invitation invitation, long invitedContactID) throws UserDoesNotExistException, InvalidUserException;


    /**
     * Removes a contact from invited Users in invitation.
     *
     * @param invitation invitation, which is to be updated
     * @param invited invited contact
     */
    void uninviteUser(Invitation invitation, RegisteredUser invited);


    /**
     * Removes a contact from invited Users in invitation.
     *
     * @param invitation invitation, which is to be updated
     * @param invitedContactID id of invited contact
     * @throws UserDoesNotExistException if a contact with this id does not exist
     */
    void uninviteUserByID(Invitation invitation, long invitedContactID) throws UserDoesNotExistException;


    /**
     * Adds the user to the participants of the {@link Call}, to which the invitation invites.
     * Updates the users active Call.
     *
     * @param user invited user
     * @param accessToken token for invitation
     * @throws InvalidUserException if user is not invited
     * @throws InvalidCallStateException if call does not exist
     * @throws InvitationDoesNotExistException if the invitation does not exist
     * @throws InvitationTokenDoesNotExistException if the accessToken does not match an invitation in the database
     */
    void joinCallByAccessToken(User user, String accessToken) throws InvalidUserException, InvalidCallStateException,
            InvitationDoesNotExistException, InvitationTokenDoesNotExistException;


    /**
     * Adds the user to the participants of the {@link Call}, to which the invitation invites.
     * Updates the users active Call.
     *
     * @param user invited user
     * @param invitation invitation for the call
     * @throws InvalidUserException if user is not invited
     * @throws InvalidCallStateException if call does not exist
     */
    void joinCall(User user, Invitation invitation) throws InvalidUserException, InvalidCallStateException;


    /**
     * Removes user from participants of his active {@link Call}.
     * Updates the users active call.
     * If the user was the last participant, the call ends.
     *
     * @param user user, who leaves the call
     * @return call, which the user left. If it is null, the call has also ended
     * @throws InvalidCallStateException if user is in no call
     */
    Call leaveCall(User user) throws InvalidCallStateException;


    /**
     * Ends all calls, that timed out.
     *
     * @param timediff Time-interval, in which is checked for timeout
     * @return list of call-ids of calls which have ended
     */
    List<Long> checkCallsForTimeoutOrEnd(long timediff);
}
