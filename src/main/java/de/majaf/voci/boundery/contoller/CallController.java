package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.service.exceptions.call.InvitationIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.user.InvalidUserException;
import de.majaf.voci.control.service.exceptions.user.UserIDDoesNotExistException;
import de.majaf.voci.entity.Call;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.security.Principal;

@Controller
public class CallController {

    @Autowired
    private ICallService callService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    public String prepareCallCreationPage(Model model, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Call activeCall = user.getActiveCall();

        if (activeCall != null) { // if the user is already in a call
            model.addAttribute("invitation", activeCall.getInvitation());
            model.addAttribute("isInitiator", activeCall.getInvitation().getInitiator());
            return "call";
        } else {
            Invitation invitation = user.getOwnedInvitation();
            if (invitation.getCall().isActive()) { // if the personal call of the user is active
                try {
                    callService.joinCall(user, invitation);
                    model.addAttribute("invitation", invitation);
                    model.addAttribute("isInitiator", true);
                    return "call";
                } catch (InvalidCallStateException | InvalidUserException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                model.addAttribute("invitingList", invitation.getInvitedUsers());
                model.addAttribute("contactsList", user.getContacts());
                model.addAttribute("invitationList", user.getActiveInvitations());
                return "prepareCall";
            }
        }
    }


    @RequestMapping(value = "/call/inviteContact", method = RequestMethod.POST)
    public String inviteContact(Model model, Principal principal, @PathParam("contactID") long contactID) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = user.getOwnedInvitation();
        try {
            callService.inviteToCall(invitation, contactID);
        } catch (InvalidUserException iue) {
            model.addAttribute("errorMsg", "Can not invite " + iue.getUser().getUserName() + " to call!");
        } catch (UserIDDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        model.addAttribute("contactsList", user.getContacts());
        model.addAttribute("invitingList", invitation.getInvitedUsers());
        model.addAttribute("invitationList", user.getActiveInvitations());
        return "prepareCall";
    }

    @RequestMapping(value = "/call/uninviteContact", method = RequestMethod.DELETE)
    public String uninviteContact(Model model, Principal principal, @PathParam("invitedID") long invitedID) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = user.getOwnedInvitation();

        try {
            callService.removeInvitedUserByID(invitation, invitedID);
        } catch (UserIDDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }

        model.addAttribute("contactsList", user.getContacts());
        model.addAttribute("invitingList", invitation.getInvitedUsers());
        model.addAttribute("invitationList", user.getActiveInvitations());
        return "prepareCall";
    }

    // TODO: Exceptions und so
    @RequestMapping(value = "/call/start", method = RequestMethod.POST)
    public String prepareCallPage(Model model, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = user.getOwnedInvitation();
        callService.startCall(invitation);
        model.addAttribute("invitation", invitation);
        model.addAttribute("isInitiator", user == invitation.getInitiator());
        return "call";
    }

    @RequestMapping(value = "/call/take", method = RequestMethod.POST)
    public String joinCall(Model model, Principal principal, @PathParam("invitationID") long invitationID) {
        RegisteredUser user = mainController.getActiveUser(principal);
        try {
            Invitation invitation = callService.loadInvitationByID(invitationID);
            callService.joinCall(user, invitation);
            model.addAttribute("invitation", invitation);
            model.addAttribute("isInitiator", false);
            return "call";
        } catch (InvitationIDDoesNotExistException | InvalidUserException | InvalidCallStateException e) {
            // TODO
            throw new IllegalArgumentException(e);
        }
    }

    // TODO: Exceptions und so
    @RequestMapping(value = "/call/leave", method = RequestMethod.DELETE)
    public String leaveCall(Model model, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        try {
            callService.leaveCall(user, user.getActiveCall().getInvitation());
        } catch (InvalidCallStateException icse) {
            throw new IllegalArgumentException(icse);
        }
        mainController.showUpdate(model, principal);
        return "main";
    }

    @RequestMapping(value = "/call/end", method = RequestMethod.DELETE)
    public String endCall(Model model, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        try {
            Call activeCall = user.getActiveCall();
            if (activeCall != null)
                callService.endCall(user, activeCall.getInvitation());
        } catch (InvalidUserException iue) { // TODO: maybe was anderes
            throw new AccessDeniedException("403 forbidden", iue);
        } catch (InvalidCallStateException icse) {
            throw new IllegalArgumentException(icse);
        }
        mainController.showUpdate(model, principal);
        return "main";
    }


}
