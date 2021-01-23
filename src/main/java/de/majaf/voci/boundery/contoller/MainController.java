package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UsernameDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String prepareMainpage(Model model, Authentication auth) throws UserDoesNotExistException {
        model.addAttribute("user", getActiveUser(auth));
        showUpdate(model, (RegisteredUser) getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/main/addRoom", method = RequestMethod.POST)
    public String createRoom(Model model, Authentication auth, @ModelAttribute("roomName") String roomName) throws UserDoesNotExistException {
        roomService.createRoom(roomName, (RegisteredUser) getActiveUser(auth));
        showUpdate(model, (RegisteredUser) getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/main/addContact", method = RequestMethod.POST)
    public String addContact(Model model, Authentication auth, @ModelAttribute("contactName") String contactName) throws UserDoesNotExistException {
        try {
            userService.addContact((RegisteredUser) getActiveUser(auth), contactName);
        } catch (InvalidUserException iue) {
            model.addAttribute("errorMsg", "Can not add " + iue.getUser().getUserName() + " as contact!");
        } catch (UsernameDoesNotExistException unee) {
            model.addAttribute("errorMsg", "User with name " + unee.getUsername() + " does not exist!");
        }
        showUpdate(model, (RegisteredUser) getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/main/deleteContact", method = RequestMethod.DELETE)
    public String deleteContact(Model model, Authentication auth, @RequestParam("contactID") long contactID) throws UserDoesNotExistException {
        try {
            userService.removeContact((RegisteredUser) getActiveUser(auth), contactID);
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        showUpdate(model, (RegisteredUser) getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String prepareInfoPage(Authentication auth, Model model) throws UserDoesNotExistException {
        model.addAttribute("user", (RegisteredUser) getActiveUser(auth));
        return "info";
    }

    private void prepareRoomsList(Model model, RegisteredUser user) {
        model.addAttribute("roomsList", user.getRooms());
        model.addAttribute("ownedRoomsList", user.getOwnedRooms());
    }

    private void prepareContactsList(Model model, RegisteredUser user) {
        model.addAttribute("contactsList", user.getContacts());
    }

    public void showUpdate(Model model, RegisteredUser user) {
        prepareRoomsList(model, user);
        prepareContactsList(model, user);
    }

    public User getActiveUser(Authentication auth) throws UserDoesNotExistException {
        if (auth != null)
            return userService.loadUserByID(((User) auth.getPrincipal()).getId());
        else return null;
    }

}

