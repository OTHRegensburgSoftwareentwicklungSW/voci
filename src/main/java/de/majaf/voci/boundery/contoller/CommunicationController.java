package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class CommunicationController {

    @Autowired
    private MainController mainController;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICallService callService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    // Done with sockets, not thymeleaf, because invitations are dynamically appended to DOM, see invitation_app.js
    @MessageMapping(value = "/{invitationID}/joinCall")
    @SendTo("/broker/{invitationID}/addedCallMember")
    public User joinCall(@DestinationVariable long invitationID, Authentication auth) throws InvitationDoesNotExistException, InvalidUserException, InvalidCallStateException, UserDoesNotExistException {
        User user = mainController.getActiveUser(auth);
        callService.joinCallByInvitationID(user, invitationID);
        simpMessagingTemplate.convertAndSend("/broker/" + user.getId() + "/joinedCall", true);
        return user;
    }
}
