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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    private RegisteredUser user;

    private List<RegisteredUser> contactSelection = new ArrayList<>();

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String prepareMainpage(Model model, Principal principal) {
        user = (RegisteredUser) userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        contactSelection.clear();
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
        } catch (InvalidUserException | UsernameNotFoundException ue) {
            model.addAttribute("errorMsg", ue.getMessage());
        }
        showUpdate(model);
        return "main";
    }

    @RequestMapping(value = "/main/deleteContact", method = RequestMethod.DELETE)
    public String deleteContact(Model model, @PathParam("contactName") String contactName){
        RegisteredUser contact = userService.removeContact(user, contactName);
        contactSelection.remove(contact);
        showUpdate(model);
        return "main";
    }

    @RequestMapping(value = "/main/selectContact", method = RequestMethod.POST)
    public String selectContacts(Model model, @ModelAttribute("selection") String selection) throws IOException {
        if (!selection.equals("")) {
            RegisteredUser contact = (RegisteredUser) userService.loadUserByUsername(selection);
            try {
                if(!contactSelection.contains(contact))
                    contactSelection.add(contact);
            } catch (UsernameNotFoundException enfe) {
                // TODO
            }
        }
        showUpdate(model);
        return "main";
    }

    private void prepareRoomsList(Model model) {
        model.addAttribute("roomsList", user.getRooms());
        model.addAttribute("ownedRoomsList", user.getOwnedRooms());
    }

    private void prepareContactsList(Model model) {
        model.addAttribute("contactsList", user.getContacts());
    }

    private void showUpdate(Model model) {
        prepareRoomsList(model);
        prepareContactsList(model);
    }

    @ModelAttribute("user")
    public RegisteredUser getActiveUser() {
        return user != null ? user : new RegisteredUser();
    }

    @ModelAttribute("contactSelection")
    public List<RegisteredUser> getContactSelection() {
        return contactSelection;
    }
}

