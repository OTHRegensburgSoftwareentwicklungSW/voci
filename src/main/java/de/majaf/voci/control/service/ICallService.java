package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.*;

import java.util.List;

public interface ICallService extends IExternalCallService{
    Invitation loadInvitationByID(long id) throws InvitationDoesNotExistException;
    Iterable<Call> getAllCalls();

    Invitation createInvitation(RegisteredUser initiator);

    Invitation saveInvitation(Invitation invitation);

    void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserDoesNotExistException;

    void uninviteUser(Invitation invitation, RegisteredUser invited);
    void uninviteUserByID(Invitation invitation, long invitedContactID) throws UserDoesNotExistException;

    void joinCallByInvitationID(User user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationDoesNotExistException;
    void joinCallByAccessToken(User user, String accessToken) throws InvalidUserException, InvalidCallStateException, InvitationDoesNotExistException, InvitationTokenDoesNotExistException;
    void joinCall(User user, Invitation invitation) throws InvalidUserException, InvalidCallStateException;

    // returns the call after leaving
    Call leaveCall(User user) throws InvalidCallStateException;

    List<Long> updateTimeout(long timediff);
}
