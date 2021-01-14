package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.exceptions.room.RoomIDDoesNotExistException;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private MainController mainController;

    @RequestMapping(value = "/room/{roomID}", method = RequestMethod.GET)
    public String prepareRoomPage(@PathVariable("roomID") long roomID, Model model, Principal principal) {
        RegisteredUser user = mainController.getActiveUser(principal);
        try {
            Room room = roomService.loadRoomByID(roomID);
            if (roomService.roomHasAsMember(room, user)) {
                model.addAttribute("user", user);
                model.addAttribute("room", room);
                return "room";
            } else throw new AccessDeniedException("User is no member");
        } catch (RoomIDDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find Room with id: " + e.getRoomID(), e);
        }
    }

}
