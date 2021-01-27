package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.exceptions.user.UsernameDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller @Scope("singleton")
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private ControllerUtils controllerUtils;

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String prepareMainpage(Model model, Authentication auth) throws UserDoesNotExistException {
        model.addAttribute("user", controllerUtils.getActiveUser(auth));
        showUpdate(model, (RegisteredUser) controllerUtils.getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/main/addRoom", method = RequestMethod.POST)
    public String createRoom(Model model, Authentication auth, @ModelAttribute("roomName") String roomName) throws UserDoesNotExistException {
        try {
            roomService.createRoom(roomName, (RegisteredUser) controllerUtils.getActiveUser(auth));
        } catch (InvalidNameException e)  {
            model.addAttribute("roomErrorMsg", "Invalid room-name. " + e.getName());
        }
        showUpdate(model, (RegisteredUser) controllerUtils.getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/main/addContact", method = RequestMethod.POST)
    public String addContact(Model model, Authentication auth, @ModelAttribute("contactName") String contactName) throws UserDoesNotExistException {
        try {
            userService.addContact((RegisteredUser) controllerUtils.getActiveUser(auth), contactName);
        } catch (InvalidUserException iue) {
            model.addAttribute("contactErrorMsg", "Can not add " + iue.getUser().getUserName() + " as contact!");
        } catch (UsernameDoesNotExistException unee) {
            model.addAttribute("contactErrorMsg", "User with name " + unee.getUsername() + " does not exist!");
        } catch (InvalidNameException ine) {
            model.addAttribute("contactErrorMsg", "Invalid username: " + ine.getName());
        }
        showUpdate(model, (RegisteredUser) controllerUtils.getActiveUser(auth));
        return "main";
    }

    @RequestMapping(value = "/main/deleteContact", method = RequestMethod.DELETE)
    public String deleteContact(Model model, Authentication auth, @RequestParam("contactID") long contactID) throws UserDoesNotExistException {
        try {
            userService.removeContact((RegisteredUser) controllerUtils.getActiveUser(auth), contactID);
        } catch (UserDoesNotExistException uidnee) {
            model.addAttribute("errorMsg", "Invalid UserID: " + uidnee.getUserID());
        }
        showUpdate(model, (RegisteredUser) controllerUtils.getActiveUser(auth));
        return "main";
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

    @ExceptionHandler(UserDoesNotExistException.class)
    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        controllerUtils.handleUserDoesNotExistException(response, e);
    }
}

