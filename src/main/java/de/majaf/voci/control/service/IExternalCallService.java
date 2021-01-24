package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;


public interface IExternalCallService {
    Invitation startCall(RegisteredUser user);
    void endCall(Invitation invitation);
    void endCallByAccessToken(RegisteredUser user, String accessToken) throws InvalidUserException, InvitationTokenDoesNotExistException;
    Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException;
}
