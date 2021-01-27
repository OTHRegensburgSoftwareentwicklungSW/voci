package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Controller
@Scope("singleton")
public class CallController {

    @Autowired
    private ICallService callService;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private Logger logger;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    public String prepareCallCreationPage(Model model, Authentication auth) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Call activeCall = user.getActiveCall();

        if (activeCall != null) { // if the user is already in a call
            dropsiController.addDropsiFilesToModel(model, user);
            model.addAttribute("call", activeCall);
            model.addAttribute("textChannel", activeCall.getTextChannel());
            model.addAttribute("user", user);
            return "call";
        } else {
            model.addAttribute("user", user);
            return "prepareCall";
        }

    }

    @RequestMapping(value = "/call/inviteContact", method = RequestMethod.POST)
    public String inviteContact(Model model, Authentication auth, @RequestParam("contactID") long contactID) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Invitation invitation = user.getOwnedInvitation();
        try {
            callService.inviteToCall(invitation, contactID);
        } catch (InvalidUserException iue) {
            model.addAttribute("errorMsg", "Can not invite " + iue.getUser().getUserName() + " to call. Not in contacts.");
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        model.addAttribute("user", user);
        return "prepareCall";
    }

    @RequestMapping(value = "/call/uninviteContact", method = RequestMethod.DELETE)
    public String uninviteContact(Model model, Authentication auth,
                                  @RequestParam("invitedID") long invitedID) throws UserDoesNotExistException {
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
    public String startCall(Model model, Authentication auth) throws UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);

        Call oldCall = user.getOwnedInvitation().getCall();
        if (oldCall != null) { // uninvite contacts remaining in a "old" call. This call is not ended, but no new members can join.
            simpMessagingTemplate.convertAndSend("/broker/" + oldCall.getId() + "/endedInvitation", true);
            simpMessagingTemplate.convertAndSend("/broker/" + oldCall.getId() + "/initiatorLeft", user);
        }

        Call call = callService.startCall(user);
        for (RegisteredUser invited : call.getInvitation().getInvitedUsers()) {
            simpMessagingTemplate.convertAndSend("/broker/" + invited.getId() + "/invited", call);
        }
        dropsiController.addDropsiFilesToModel(model, user);
        model.addAttribute("call", call);
        model.addAttribute("textChannel", call.getTextChannel());
        model.addAttribute("user", user);
        return "call";
    }

    @RequestMapping(value = "/call/end", method = RequestMethod.DELETE)
    public String endCall(Authentication auth, Model model, HttpServletResponse response) throws UserDoesNotExistException, IOException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        try {
            Call activeCall = user.getActiveCall();
            if (activeCall != null) {
                callService.endCallAuthenticated(activeCall, user);
                simpMessagingTemplate.convertAndSend("/broker/" + activeCall.getId() + "/endedInvitation", true);
                simpMessagingTemplate.convertAndSend("/broker/" + activeCall.getId() + "/endedCall", false);
            }
            model.addAttribute("user", user);
            return "prepareCall";
        } catch (InvalidUserException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User : " + e.getUser().getUserName() + " has no right to end this call. No initiator");
            return "/error/404";
        }
    }

    @RequestMapping(value = "/call/leave/{callID}", method = RequestMethod.DELETE)
    public String leaveCall(@PathVariable("callID") long callID,
                            Model model, Authentication auth,
                            HttpServletRequest req,
                            HttpServletResponse response) throws ServletException, IOException {
        try {
            User user = controllerUtils.getActiveUser(auth);
            Call call = callService.leaveCall(user);
            controllerUtils.sendSocketLeaveCallMessages(user, call, callID);
            if (user instanceof GuestUser) {
                simpMessagingTemplate.convertAndSend("/broker/" + callID + "/removedCallMember", user);
                req.logout();
                return "leftCall";
            } else {
                model.addAttribute("user", user);
                return "prepareCall";
            }
        } catch (InvalidCallStateException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is in no call.");
        } catch (UserDoesNotExistException userDoesNotExistException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User does not exist.");
        }
        return "leftCall";
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        controllerUtils.handleUserDoesNotExistException(response, e);
    }
}