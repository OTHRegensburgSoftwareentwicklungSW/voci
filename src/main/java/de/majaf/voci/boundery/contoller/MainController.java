package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UsernameDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.security.Principal;

@Controller
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String prepareMainpage(Model model, Principal principal) {
        model.addAttribute("user", getActiveUser(principal));
        showUpdate(model, principal);
        return "main";
    }

    @RequestMapping(value = "/main/addRoom", method = RequestMethod.POST)
    public String createRoom(Model model, Principal principal, @ModelAttribute("roomName") String roomName) {
        roomService.createRoom(roomName, getActiveUser(principal));
        showUpdate(model, principal);
        return "main";
    }

    @RequestMapping(value = "/main/addContact", method = RequestMethod.POST)
    public String addContact(Model model, Principal principal, @ModelAttribute("contactName") String contactName) {
        try {
            userService.addContact(getActiveUser(principal), contactName);
        } catch (InvalidUserException iue) {
            model.addAttribute("errorMsg", "Can not add " + iue.getUser().getUserName() + " as contact!");
        } catch (UsernameDoesNotExistException unee) {
            model.addAttribute("errorMsg", "User with name " + unee.getUsername() + " does not exist!");
        }
        showUpdate(model, principal);
        return "main";
    }

    @RequestMapping(value = "/main/deleteContact", method = RequestMethod.DELETE)
    public String deleteContact(Model model, Principal principal, @PathParam("contactID") long contactID){
        try {
            userService.removeContact(getActiveUser(principal), contactID);
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        showUpdate(model, principal);
        return "main";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String prepareInfoPage(Principal principal, Model model) {
        model.addAttribute("user", getActiveUser(principal));
        return "info";
    }

    private void prepareRoomsList(Model model, RegisteredUser user) {
        model.addAttribute("roomsList", user.getRooms());
        model.addAttribute("ownedRoomsList", user.getOwnedRooms());
    }

    private void prepareContactsList(Model model, RegisteredUser user) {
        model.addAttribute("contactsList", user.getContacts());
    }

    public void showUpdate(Model model, Principal principal) {
        RegisteredUser user = getActiveUser(principal);
        prepareRoomsList(model, user);
        prepareContactsList(model, user);
    }

    public RegisteredUser getActiveUser(Principal principal) {
        return (RegisteredUser) userService.loadUserByUsername(principal.getName());
    }

}

