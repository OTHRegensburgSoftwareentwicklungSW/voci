package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Controller @Scope("session")
public class CallController {

    @Autowired
    private ICallService callService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private MainController mainController;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private Logger logger;

    // TODO: inherit from this and create producer method
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    public String prepareCallCreationPage(Model model, Authentication auth) throws InvalidCallStateException, InvalidUserException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Call activeCall = user.getActiveCall();

        if (activeCall != null) { // if the user is already in a call
            dropsiController.addDropsiFilesToModel(model, user);
            model.addAttribute("invitation", activeCall.getInvitation());
            model.addAttribute("textChannel", activeCall.getTextChannel());
            model.addAttribute("user", user);
            return "call";
        } else {
            Invitation invitation = user.getOwnedInvitation();
            if (invitation.getCall().isActive()) { // if the personal call of the user is active
                callService.joinCall(user, invitation);
                dropsiController.addDropsiFilesToModel(model, user);
                model.addAttribute("invitation", invitation);
                model.addAttribute("textChannel", invitation.getCall().getTextChannel());
                model.addAttribute("user", user);
                simpMessagingTemplate.convertAndSend("/broker/" + invitation.getId() + "/addedCallMember", user);
                return "call";
            } else {
                model.addAttribute("invitingList", invitation.getInvitedUsers());
                model.addAttribute("user", user);
                return "prepareCall";
            }
        }
    }


    @RequestMapping(value = "/call/inviteContact", method = RequestMethod.POST)
    public String inviteContact(Model model, Authentication auth, @RequestParam("contactID") long contactID) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
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
    public String uninviteContact(Model model, Authentication auth, @RequestParam("invitedID") long invitedID) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
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
    public String startCall(Model model, Authentication auth) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);

        // make sure that all invitations of previous calls get removed in GUI
        simpMessagingTemplate.convertAndSend("/broker/" + user.getOwnedInvitation().getId() + "/endedInvitation", true);

        Invitation invitation = callService.startCall(user);
        for (RegisteredUser invited : invitation.getInvitedUsers()) {
            simpMessagingTemplate.convertAndSend("/broker/" + invited.getId() + "/invited", invitation);
        }
        dropsiController.addDropsiFilesToModel(model, user);
        model.addAttribute("invitation", invitation);
        model.addAttribute("textChannel", invitation.getCall().getTextChannel());
        model.addAttribute("user", user);
        logger.info("User " + user.getUserName() + " started a call.");
        return "call";
    }

    @RequestMapping(value = "/call/end", method = RequestMethod.DELETE)
    public String endCall(Authentication auth, Model model) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
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

    @RequestMapping(value = "/call/leave/{invitationID}/{userID}", method = RequestMethod.DELETE)
    public String leaveCall(@PathVariable("invitationID") long invitationID, @PathVariable("userID")long userID, // TODO: use principal for this
                            Model model, HttpServletRequest req) throws UserDoesNotExistException, InvalidCallStateException, ServletException {
        User user = userService.loadUserByID(userID);
        boolean callStillActive = callService.leaveCall(user);
        if (!callStillActive) {
            simpMessagingTemplate.convertAndSend("/broker/" + invitationID + "/endedInvitation", true);
        } else {
            simpMessagingTemplate.convertAndSend("/broker/" + invitationID + "/removedCallMember", user);
        }
        if (user instanceof GuestUser) {
            req.logout();
            return "leftCall";
        } else {
            mainController.showUpdate(model, (RegisteredUser) user);
            return "main";
        }
    }
}