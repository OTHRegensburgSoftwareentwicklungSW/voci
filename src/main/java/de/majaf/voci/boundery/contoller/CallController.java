package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.ICallService;
import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.control.service.exceptions.UserIDDoesNotExistException;
import de.majaf.voci.control.service.exceptions.UsernameDoesNotExistException;
import de.majaf.voci.entity.Invitation;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
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
        model.addAttribute("contactsList", user.getContacts());
        model.addAttribute("invitingList", user.getOwnedInvitation().getInvitedUsers());
        return "call";
    }


    @RequestMapping(value = "/call/inviteContact", method = RequestMethod.POST)
    public String inviteContact(Model model, Principal principal, @PathParam("contactID") long contactID){
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
        return "call";
    }

    @RequestMapping(value = "/call/uninviteContact", method = RequestMethod.DELETE)
    public String uninviteContact(Model model, Principal principal, @PathParam("invitedID") long invitedID){
        RegisteredUser user = mainController.getActiveUser(principal);
        Invitation invitation = user.getOwnedInvitation();

        try {
            callService.removeInvitedUserByID(invitation, invitedID);
        } catch (UserIDDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }

        model.addAttribute("contactsList", user.getContacts());
        model.addAttribute("invitingList", invitation.getInvitedUsers());
        return "call";
    }


}
