package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CallRestController {

    @Autowired
    private ICallService callService;

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/api/startCall", method = RequestMethod.GET)
    public Invitation startCall(@RequestParam String securityToken, HttpServletRequest req) throws UserTokenDoesNotExistException, InvitationDoesNotExistException {
        RegisteredUser user = userService.authenticateUser(securityToken, req);
        return callService.startCall(user);
    }
}
