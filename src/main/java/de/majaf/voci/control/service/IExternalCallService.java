package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;


public interface IExternalCallService {
    String startCall(String userToken) throws UserTokenDoesNotExistException, InvitationDoesNotExistException;
    void joinCall(String userToken, String callURL);
}
