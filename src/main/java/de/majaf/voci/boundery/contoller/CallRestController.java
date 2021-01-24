package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;
import de.majaf.voci.control.service.IExternalCallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController @Scope("session")
public class CallRestController {

    @Autowired
    private IExternalCallService callService;

    @Autowired
    private IUserService userService;

    @Autowired
    ControllerUtils controllerUtils;

    @RequestMapping(value = "/api/startCall", method = RequestMethod.POST)
    public Invitation startCall(@RequestHeader String securityToken, HttpServletRequest req) throws UserTokenDoesNotExistException {
        RegisteredUser user = userService.loadUserBySecurityToken(securityToken);
        controllerUtils.authenticateUser(user, req);
        return callService.startCall(user).getInvitation();
    }

    @RequestMapping(value = "/api/endCall", method = RequestMethod.DELETE)
    public void endCall(@RequestHeader String securityToken, @RequestParam String accessToken, HttpServletRequest req) throws UserTokenDoesNotExistException, InvitationTokenDoesNotExistException, InvalidUserException {
        RegisteredUser user = userService.loadUserBySecurityToken(securityToken);
        controllerUtils.authenticateUser(user, req);
        callService.endCallByAccessToken(user, accessToken);
    }

    @ExceptionHandler(UserTokenDoesNotExistException.class)
    public void handleUserTokenDoesNotExistException(UserTokenDoesNotExistException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User with this Security-Token does not exist. Token: " + e.getUserToken());
    }

    @ExceptionHandler(InvitationTokenDoesNotExistException.class)
    public void handleInvitationTokenDoesNotExistException(InvitationTokenDoesNotExistException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "A invitation with this Access-Token does not exist. Token: " + e.getAccessToken());
    }

    @ExceptionHandler(InvalidUserException.class)
    public void handleInvalidUserException(InvalidUserException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "User " + e.getUser().getUserName() + " is not initiator. Not allowed to end this call.");
    }
}
