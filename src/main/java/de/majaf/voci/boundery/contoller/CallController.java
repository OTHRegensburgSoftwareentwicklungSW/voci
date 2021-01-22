package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CallController {

    @Autowired
    private ICallService callService;

    @Autowired
    private MainController mainController;

    // TODO: inherit from this and create producer method
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    public String prepareCallCreationPage(Model model, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Call activeCall = user.getActiveCall();

        if (activeCall != null) { // if the user is already in a call
            model.addAttribute("invitation", activeCall.getInvitation());
            model.addAttribute("textChannel", activeCall.getTextChannel());
            model.addAttribute("user", user);
            return "call";
        } else {
            Invitation invitation = user.getOwnedInvitation();
            if (invitation.getCall().isActive()) { // if the personal call of the user is active
                try {
                    callService.joinCall(user, invitation);
                    model.addAttribute("invitation", invitation);
                    model.addAttribute("textChannel", invitation.getCall().getTextChannel());
                    model.addAttribute("user", user);
                    simpMessagingTemplate.convertAndSend("/broker/" + invitation.getId() + "/addedCallMember", user);
                    return "call";
                } catch (InvalidCallStateException | InvalidUserException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                model.addAttribute("invitingList", invitation.getInvitedUsers());
                model.addAttribute("user", user);
                return "prepareCall";
            }
        }
    }


    @RequestMapping(value = "/call/inviteContact", method = RequestMethod.POST)
    public String inviteContact(Model model, Principal principal, @RequestParam("contactID") long contactID) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = user.getOwnedInvitation();
        try {
            callService.inviteToCall(invitation, contactID);
        } catch (InvalidUserException iue) {
            model.addAttribute("errorMsg", "Can not invite " + iue.getUser().getUserName() + " to call!");
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        model.addAttribute("invitingList", invitation.getInvitedUsers());
        model.addAttribute("user", user);
        return "prepareCall";
    }

    @RequestMapping(value = "/call/uninviteContact", method = RequestMethod.DELETE)
    public String uninviteContact(Model model, Principal principal, @RequestParam("invitedID") long invitedID) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = user.getOwnedInvitation();

        try {
            callService.removeInvitedUserByID(invitation, invitedID);
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        model.addAttribute("invitingList", invitation.getInvitedUsers());
        model.addAttribute("user", user);
        return "prepareCall";
    }

    @RequestMapping(value = "/call/start", method = RequestMethod.POST)
    public String startCall(Model model, Principal principal) throws InvalidCallStateException {
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = callService.startCall(user);
        for (RegisteredUser invited : invitation.getInvitedUsers()) {
            simpMessagingTemplate.convertAndSend("/broker/" + invited.getId() + "/invited", invitation);
        }
        model.addAttribute("invitation", invitation);
        model.addAttribute("textChannel", invitation.getCall().getTextChannel());
        model.addAttribute("user", user);
        return "call";
    }

    @RequestMapping(value = "/call/end", method = RequestMethod.DELETE)
    public String endCall(Principal principal, Model model) throws InvalidCallStateException {
        RegisteredUser user = mainController.getActiveUser(principal);
        Call activeCall = user.getActiveCall();
        if (activeCall != null) {
            callService.endCall(user.getOwnedInvitation());
            simpMessagingTemplate.convertAndSend("/broker/" + user.getOwnedInvitation().getId() + "/endedInvitation", true);
            simpMessagingTemplate.convertAndSend("/broker/" + user.getOwnedInvitation().getId() + "/endedCall", true);
        }
        model.addAttribute("invitingList", user.getOwnedInvitation().getInvitedUsers());
        model.addAttribute("user", user);
        return "prepareCall";
    }
}