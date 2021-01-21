package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;


public interface IExternalCallService {
    Invitation startCall(RegisteredUser user) throws InvalidCallStateException;
    void endCall(Invitation invitation) throws InvalidCallStateException;
}
