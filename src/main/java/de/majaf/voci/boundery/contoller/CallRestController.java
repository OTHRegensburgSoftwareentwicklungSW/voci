package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UserTokenDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.IExternalCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallRestController {

    @Autowired
    IExternalCallService callService;

    @RequestMapping(value = "/restapi/call/start", method = RequestMethod.POST)
    public String startCall(@RequestBody String userToken) throws UserTokenDoesNotExistException, InvitationIDDoesNotExistException {
        return callService.generateCallURL(userToken);
    }
}
