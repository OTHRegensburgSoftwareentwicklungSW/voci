package de.majaf.voci.control.service.impl;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IExternalCallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalCallService implements IExternalCallService {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICallService callService;

    @Override
    public String startCall(String userToken) throws UserTokenDoesNotExistException, InvitationDoesNotExistException {
        RegisteredUser user = userService.loadUserBySecurityToken(userToken);

        // TODO: Exception Handling
        callService.startCall(user.getOwnedInvitation().getId());
        return user.getOwnedInvitation().getAccessToken();
    }

    @Override
    public void joinCall(String userToken, String callURL) {

    }
}
