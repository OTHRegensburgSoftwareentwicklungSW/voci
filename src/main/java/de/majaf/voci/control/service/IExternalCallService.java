package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;


public interface IExternalCallService {
    Invitation startCall(RegisteredUser user) throws InvalidCallStateException;
    void endCall(Invitation invitation) throws InvalidCallStateException;
    void endCallByAccessToken(RegisteredUser user, String accessToken) throws InvalidUserException, InvalidCallStateException, InvitationTokenDoesNotExistException;
    Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException;
}
