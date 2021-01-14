package de.majaf.voci.control.service;


import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserIDDoesNotExistException;
import de.majaf.voci.entity.GuestUser;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;

public interface ICallService extends IExternalCallService{
    Invitation createInvitation(RegisteredUser initiator);
    Invitation loadInvitationByID(long id) throws InvitationIDDoesNotExistException;
    Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException;

    void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserIDDoesNotExistException;

    void removeInvitedUser(Invitation invitation, RegisteredUser invited);
    void removeInvitedUserByID(Invitation invitation, long invitedContactID) throws UserIDDoesNotExistException;

    void startCall(long invitationID) throws InvitationIDDoesNotExistException;
    void joinCallByInvitationID(User user, long InvitationID) throws InvalidUserException, InvalidCallStateException, InvitationIDDoesNotExistException;
    void joinCall(User user, Invitation invitation) throws InvalidUserException, InvalidCallStateException;
    GuestUser createGuestUserAndJoinCall(String accessToken) throws InvitationTokenDoesNotExistException, InvalidCallStateException, InvalidUserException;

    boolean isUserInvited(User user, Invitation invitation);
    boolean isUserInvited(User user, long invitationID) throws InvitationIDDoesNotExistException;

    boolean isUserInCall(User user, long invitationID) throws InvitationIDDoesNotExistException;

    // returns if call is still active after leaving TODO maybe
    boolean leaveCallByInvitationID(User user, long invitationID) throws InvalidCallStateException, InvitationIDDoesNotExistException;
    boolean leaveCall(User user, Invitation invitation) throws InvalidCallStateException;

    void endCallByInvitationID(RegisteredUser user, long invitationID) throws InvalidUserException, InvalidCallStateException, InvitationIDDoesNotExistException;
    void endCall(Invitation invitation) throws InvalidCallStateException;
    String generateAccessToken(Invitation invitation);
}
