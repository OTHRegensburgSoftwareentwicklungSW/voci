package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationTokenDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;
import de.majaf.voci.control.service.IExternalCallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/api")
@RestController @Scope("singleton")
public class CallRestController {

    @Autowired
    private IExternalCallService callService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/startCall", method = RequestMethod.POST)
    public Invitation startCall(@RequestHeader String securityToken, HttpServletRequest req) throws UserTokenDoesNotExistException {
        RegisteredUser user = userService.loadUserBySecurityToken(securityToken);
        controllerUtils.authenticateUser(user, req);
        return callService.startCall(user).getInvitation();
    }

    @RequestMapping(value = "/endCall", method = RequestMethod.DELETE)
    public void endCall(@RequestHeader String securityToken, @RequestParam String accessToken, HttpServletRequest req,
                        HttpServletResponse response) throws UserTokenDoesNotExistException, InvitationTokenDoesNotExistException, InvalidCallStateException, IOException {
        RegisteredUser user = userService.loadUserBySecurityToken(securityToken);
        controllerUtils.authenticateUser(user, req);
        Call call = callService.loadCallByToken(accessToken);
        try {
            callService.endCallAuthenticated(call, user);
            simpMessagingTemplate.convertAndSend("/broker/" + call.getId() + "/endedInvitation", true);
            simpMessagingTemplate.convertAndSend("/broker/" + call.getId() + "/endedCall", false);
        } catch(InvalidUserException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User " + e.getUser().getUserName() + " is not initiator. Not allowed to end this call.");
        }
    }

    @ExceptionHandler(UserTokenDoesNotExistException.class)
    public void handleUserTokenDoesNotExistException(UserTokenDoesNotExistException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User with this Security-Token does not exist. Token: " + e.getUserToken());
    }

    @ExceptionHandler(InvalidCallStateException.class)
    public void handleInvalidCallStateException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "The call you are looking for is currently not active.");
    }

    @ExceptionHandler(InvitationTokenDoesNotExistException.class)
    public void handleInvitationTokenDoesNotExistException(InvitationTokenDoesNotExistException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "A invitation with this Access-Token does not exist. Token: " + e.getAccessToken());
    }
}
