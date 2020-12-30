package de.majaf.voci.control.service;


import de.majaf.voci.control.service.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.service.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.exceptions.user.UserIDDoesNotExistException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;

public interface ICallService extends IExternalCallService{
    Invitation createInvitation(RegisteredUser initiator);
    Invitation loadInvitationByID(long invitationID) throws InvitationIDDoesNotExistException;

    void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserIDDoesNotExistException;

    void removeInvitedUser(Invitation invitation, RegisteredUser invited);
    void removeInvitedUserByID(Invitation invitation, long invitedContactID) throws UserIDDoesNotExistException;

    void startCall(Invitation invitation);
    void joinCall(RegisteredUser user, Invitation invitation) throws InvalidUserException, InvalidCallStateException;
    void leaveCall(RegisteredUser user, Invitation invitation) throws InvalidCallStateException;
    void endCall(User user, Invitation invitation) throws InvalidUserException, InvalidCallStateException;
}
