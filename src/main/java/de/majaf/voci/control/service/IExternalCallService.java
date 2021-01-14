package de.majaf.voci.control.service;

import de.majaf.voci.control.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;


public interface IExternalCallService {
    String generateCallURL(String userToken) throws UserTokenDoesNotExistException, InvitationIDDoesNotExistException;
    void joinCall(String userToken, String callURL);
}
