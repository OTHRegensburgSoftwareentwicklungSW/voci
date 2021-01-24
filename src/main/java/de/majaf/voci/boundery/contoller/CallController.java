package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.InvitationDoesNotExistException;
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

@Controller
@Scope("session")
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
    public String prepareCallCreationPage(Model model, Authentication auth) {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Call activeCall = user.getActiveCall();

        if (activeCall != null) { // if the user is already in a call
            dropsiController.addDropsiFilesToModel(model, user);
            model.addAttribute("call", activeCall);
            model.addAttribute("textChannel", activeCall.getTextChannel());
            model.addAttribute("user", user);
            return "call";
        } else {
            Invitation invitation = user.getOwnedInvitation();
            model.addAttribute("user", user);
            return "prepareCall";
        }

    }


    @RequestMapping(value = "/call/inviteContact", method = RequestMethod.POST)
    public String inviteContact(Model model, Authentication auth, @RequestParam("contactID") long contactID) {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Invitation invitation = user.getOwnedInvitation();
        try {
            callService.inviteToCall(invitation, contactID);
        } catch (InvalidUserException iue) {
            model.addAttribute("errorMsg", "Can not invite " + iue.getUser().getUserName() + " to call!");
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        model.addAttribute("user", user);
        return "prepareCall";
    }

    @RequestMapping(value = "/call/uninviteContact", method = RequestMethod.DELETE)
    public String uninviteContact(Model model, Authentication auth, @RequestParam("invitedID") long invitedID) {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Invitation invitation = user.getOwnedInvitation();
        try {
            callService.uninviteUserByID(invitation, invitedID);
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        model.addAttribute("user", user);
        return "prepareCall";
    }

    @RequestMapping(value = "/call/start", method = RequestMethod.POST)
    public String startCall(Model model, Authentication auth) {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);

        Call call = callService.startCall(user);
        for (RegisteredUser invited : call.getInvitation().getInvitedUsers()) {
            simpMessagingTemplate.convertAndSend("/broker/" + invited.getId() + "/invited", call);
        }
        dropsiController.addDropsiFilesToModel(model, user);
        model.addAttribute("call", call);
        model.addAttribute("textChannel", call.getTextChannel());
        model.addAttribute("user", user);
        logger.info("User " + user.getUserName() + " started a call.");
        return "call";
    }

    @RequestMapping(value = "/call/end", method = RequestMethod.DELETE)
    public String endCall(Authentication auth, Model model) {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Call activeCall = user.getActiveCall();
        if (activeCall != null) {
            callService.endCall(activeCall);
            simpMessagingTemplate.convertAndSend("/broker/" + activeCall.getId() + "/endedInvitation", true);
            simpMessagingTemplate.convertAndSend("/broker/" + activeCall.getId() + "/endedCall", true);
        }
        model.addAttribute("user", user);
        return "prepareCall";
    }

    @RequestMapping(value = "/call/leave/{callID}/{userID}", method = RequestMethod.DELETE)
    public String leaveCall(@PathVariable("callID") long callID, @PathVariable("userID") long userID, // TODO: use principal for this
                            Model model, HttpServletRequest req) throws UserDoesNotExistException, InvalidCallStateException, ServletException {
        User user = userService.loadUserByID(userID);
        Call call = callService.leaveCall(user);

        if (call == null) {
            simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedInvitation", true);
        } else {
            if (user instanceof RegisteredUser)
                if (call.getInvitation() != null && call.getInvitation().equals(((RegisteredUser) user).getOwnedInvitation())) {
                    simpMessagingTemplate.convertAndSend("/broker/" + callID + "/initiatorLeft", user);
                    simpMessagingTemplate.convertAndSend("/broker/" + callID + "/endedInvitation", true);
                } else {
                    simpMessagingTemplate.convertAndSend("/broker/" + callID + "/removedCallMember", user);
                }
        }
        if (user instanceof GuestUser) {
            simpMessagingTemplate.convertAndSend("/broker/" + callID + "/removedCallMember", user);
            req.logout();
            return "leftCall";
        } else {
            model.addAttribute("user", user);
            return "prepareCall";
        }
    }
}