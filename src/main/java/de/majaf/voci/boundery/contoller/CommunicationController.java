package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.service.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.user.InvalidUserException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.wrapper.RegUserInvitationWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
    private ICallService callService;

    // user for special cases in sendTo (see startCall(Principal))
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(value = "/startCall")
    // actual response would be
    // @SendTo("/broker/{userID}/startedCall")
    // AND
    // @SendTo("/broker/{userID}/invited")
    public void startCall(Principal principal) throws InvitationIDDoesNotExistException {
        RegisteredUser user = mainController.getActiveUser(principal);

        Invitation invitation = user.getOwnedInvitation();
        callService.startCall(invitation.getId());

        // cannot use destination Variable in @SendTo only. So this is a workaround.
        simpMessagingTemplate.convertAndSend("/broker/" + user.getId() + "/startedCall", true);

        for (RegisteredUser invited : invitation.getInvitedUsers()) {
            simpMessagingTemplate.convertAndSend("/broker/" + invited.getId() + "/invited",
                    new RegUserInvitationWrapper(invitation.getId(), user.getUserName()));
        }
    }

    @MessageMapping(value = "/{invitationID}/joinCall")
    @SendTo("/broker/{invitationID}/addedCallMember")
    public String joinCall(@DestinationVariable long invitationID, Principal principal) {
        try {
            RegisteredUser user = mainController.getActiveUser(principal);
            callService.joinCallByInvitationID(user, invitationID);
            simpMessagingTemplate.convertAndSend("/broker/" + user.getId() + "/joinedCall", true);
            return user.getUserName();
        } catch (InvitationIDDoesNotExistException | InvalidUserException | InvalidCallStateException e) {
            // TODO
            throw new IllegalArgumentException(e);
        }
    }

    @MessageMapping(value = "/{invitationID}/leaveCall")
    @SendTo(value = "/broker/{invitationID}/removedCallMember")
    public String leaveCall(@DestinationVariable long invitationID, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        try {
            boolean callStillActive = callService.leaveCallByInvitationID(user, invitationID);
            simpMessagingTemplate.convertAndSend("/broker/" + user.getId() + "/leftCall", true);
            if(!callStillActive)
                simpMessagingTemplate.convertAndSend("/broker/" + invitationID + "/endedInvitation", true);

            return user.getUserName();
        } catch (InvalidCallStateException | InvitationIDDoesNotExistException e) {
            // TODO
            throw new IllegalArgumentException(e);
        }
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
        } catch (InvalidCallStateException | InvitationIDDoesNotExistException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
