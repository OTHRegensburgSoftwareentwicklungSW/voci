package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;


public interface IExternalCallService {
    Call startCall(RegisteredUser user);

    void endCall(Call call);

    void endCallAuthenticated(Call call, User user) throws InvalidUserException;

    Invitation loadInvitationByToken(String accessToken) throws InvitationTokenDoesNotExistException;

    Call loadCallByToken(String accessToken) throws InvitationTokenDoesNotExistException, InvalidCallStateException;

    void endInvitation(Invitation invitation);
}
