package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;


public interface IExternalCallService {
    Call startCall(RegisteredUser user);
    void endCall(Call call);
    void endCallByAccessToken(RegisteredUser user, String accessToken) throws InvalidUserException, InvitationTokenDoesNotExistException;
    Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException;
    void endInvitation(Invitation invitation);
}
