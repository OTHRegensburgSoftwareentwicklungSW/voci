package de.majaf.voci.control.service;


import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.UsernameDoesNotExistException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;

public interface ICallService extends IExternalCallService{
    Invitation createInvitation(RegisteredUser initiator);

    void inviteToCall(Invitation invitation, long invitedContactID) throws InvalidUserException, UserIDDoesNotExistException;

    void removeInvitedUser(Invitation invitation, RegisteredUser invited);
    void removeInvitedUserByID(Invitation invitation, long invitedContactID) throws UserIDDoesNotExistException;
}
