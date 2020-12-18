package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.control.service.exceptions.InvalidUserException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    private RegisteredUser user;

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String prepareMainpage(Model model, Principal principal) {
        user = (RegisteredUser) userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        showUpdate(model);
        return "main";
    }

    @RequestMapping(value = "/main/addRoom", method = RequestMethod.POST)
    public String createRoom(Model model, @ModelAttribute("roomName") String roomName) {
        roomService.createRoom(roomName, user);
        showUpdate(model);
        return "main";
    }

    @RequestMapping(value = "/main/addContact", method = RequestMethod.POST)
    public String addContact(Model model, @ModelAttribute("contactName") String contactName) {
        try {
            userService.addContact(user, contactName);
        } catch (InvalidUserException | UsernameNotFoundException ue ) {
            model.addAttribute("errorMsg", ue.getMessage());
        }
        showUpdate(model);
        return "main";
    }

    @RequestMapping(value = "/main/deleteContact", method = RequestMethod.DELETE)
    public String deleteContact(Model model, @PathParam("contactName") String contactName) {
        userService.removeContact(user, contactName);
        showUpdate(model);
        return "main";
    }

    private void showRoomsList(Model model) {
        List<Room> roomsList = user.getRooms();
        List<Room> ownedRoomsList = user.getOwnedRooms();
        model.addAttribute("roomsList", roomsList);
        model.addAttribute("ownedRoomsList", ownedRoomsList);
    }

    private void showContactsList(Model model) {
        List<RegisteredUser> contactsList = user.getContacts();
        model.addAttribute("contactsList", contactsList);
    }

    private void showUpdate(Model model) {
        showRoomsList(model);
        showContactsList(model);
    }

    @ModelAttribute("user")
    public RegisteredUser getActiveUser() {
        return user != null ? user : new RegisteredUser();
    }

}
