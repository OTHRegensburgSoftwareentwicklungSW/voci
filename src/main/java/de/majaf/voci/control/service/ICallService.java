package de.majaf.voci.control.service;


import de.majaf.voci.control.service.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.service.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.exceptions.user.UserIDDoesNotExistException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;

import java.util.List;

public interface ICallService extends IExternalCallService{
    Invitation createInvitation(RegisteredUser initiator);
    Invitation loadInvitationByID(long invitationID) throws InvitationIDDoesNotExistException;

    void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserIDDoesNotExistException;

    void removeInvitedUser(Invitation invitation, RegisteredUser invited);
    void removeInvitedUserByID(Invitation invitation, long invitedContactID) throws UserIDDoesNotExistException;

    void startCall(long invitationID) throws InvitationIDDoesNotExistException;
    void joinCallByInvitationID(RegisteredUser user, long InvitationID) throws InvalidUserException, InvalidCallStateException, InvitationIDDoesNotExistException;
    void joinCall(RegisteredUser user, Invitation invitation) throws InvalidUserException, InvalidCallStateException;

    boolean isUserInvited(RegisteredUser user, Invitation invitation);
    boolean isUserInvited(RegisteredUser user, long invitationID) throws InvitationIDDoesNotExistException;

    boolean isUserInCall(User user, long invitationID) throws InvitationIDDoesNotExistException;

    // returns if call is still active after leaving TODO maybe
    boolean leaveCallByInvitationID(RegisteredUser user, long invitationID) throws InvalidCallStateException, InvitationIDDoesNotExistException;
    boolean leaveCall(RegisteredUser user, Invitation invitation) throws InvalidCallStateException;

    void endCallByInvitationID(RegisteredUser user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationIDDoesNotExistException;
    void endCall(Invitation invitation) throws InvalidCallStateException;
}
