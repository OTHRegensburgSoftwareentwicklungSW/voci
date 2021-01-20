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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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
    public User joinCall(@DestinationVariable long invitationID, Principal principal) throws InvitationDoesNotExistException, InvalidUserException, InvalidCallStateException {
        RegisteredUser user = mainController.getActiveUser(principal);
        callService.joinCallByInvitationID(user, invitationID);
        simpMessagingTemplate.convertAndSend("/broker/" + user.getId() + "/joinedCall", true);
        return user;
    }

    @MessageMapping(value = "/{invitationID}/leaveCall")
    @SendTo(value = "/broker/{invitationID}/removedCallMember")
    public User leaveCall(@DestinationVariable long invitationID, long userID) throws UserDoesNotExistException, InvitationDoesNotExistException, InvalidCallStateException {
        User user = userService.loadUserByID(userID);
        boolean callStillActive = callService.leaveCallByInvitationID(user, invitationID);
        simpMessagingTemplate.convertAndSend("/broker/" + user.getId() + "/leftCall", true);
        if (!callStillActive)
            simpMessagingTemplate.convertAndSend("/broker/" + invitationID + "/endedInvitation", true);
        return user;
    }


    @MessageMapping(value = "/{invitationID}/endCall")
    @SendTo("/broker/{invitationID}/endedCall")
    public Boolean endCall(@DestinationVariable long invitationID, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        try {
            Call activeCall = user.getActiveCall();
            if (activeCall != null) {
                callService.endCallByInvitationID(user, invitationID);
                simpMessagingTemplate.convertAndSend("/broker/" + invitationID + "/endedInvitation", true);
                return true;
            } else return null;
        } catch (InvalidUserException iue) { // TODO: maybe was anderes
            throw new AccessDeniedException("403 forbidden", iue);
        } catch (InvalidCallStateException | InvitationDoesNotExistException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
