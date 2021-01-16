package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserIDDoesNotExistException;
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

import javax.websocket.server.PathParam;
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

    // TODO: Exception Handling
    @RequestMapping(value = "/room/{roomID}/invite", method = RequestMethod.GET)
    public String prepareRoomInvitationPage(@PathVariable("roomID") long roomID,
                                            Model model, Principal principal) throws RoomIDDoesNotExistException {
        RegisteredUser user = mainController.getActiveUser(principal);
        Room room = roomService.loadRoomByID(roomID);
        model.addAttribute("user", user);
        model.addAttribute("room", room);
        return "roomInvite";
    }

    // TODO Exceptions
    @RequestMapping(value = "/room/{roomID}/inviteContact", method = RequestMethod.POST)
    public String inviteContact(@PathVariable("roomID") long roomID, Model model, Principal principal, @PathParam("contactID") long contactID) throws RoomIDDoesNotExistException, UserIDDoesNotExistException, InvalidUserException {
        RegisteredUser user = mainController.getActiveUser(principal);
        Room room = roomService.loadRoomByID(roomID);
        roomService.addMemberToRoom(room, contactID, user);
        model.addAttribute("user", user);
        model.addAttribute("room", room);
        return "room";
    }

    // TODO Exceptions
    @RequestMapping(value = "/room/{roomID}/removeMember", method = RequestMethod.DELETE)
    public String removeMember(@PathVariable("roomID") long roomID, Model model, Principal principal, @PathParam("memberID") long memberID) throws RoomIDDoesNotExistException, UserIDDoesNotExistException, InvalidUserException {
        RegisteredUser user = mainController.getActiveUser(principal);
        Room room = roomService.loadRoomByID(roomID);
        roomService.removeMemberFromRoom(room, memberID, user);
        model.addAttribute("user", user);
        model.addAttribute("room", room);
        return "room";
    }

    // TODO Exceptions
    @RequestMapping(value = "/room/{roomID}/delete", method = RequestMethod.DELETE)
    public String deleteRoom(@PathVariable("roomID") long roomID, Model model, Principal principal) throws RoomIDDoesNotExistException, InvalidUserException {
        RegisteredUser user = mainController.getActiveUser(principal);
        roomService.deleteRoom(roomID, user);
        mainController.showUpdate(model, principal);
        return "main";
    }

    // TODO Exceptions
    @RequestMapping(value = "/room/{roomID}/leave", method = RequestMethod.DELETE)
    public String leaveRoom(@PathVariable("roomID") long roomID, Model model, Principal principal) throws RoomIDDoesNotExistException, InvalidUserException {
        RegisteredUser user = mainController.getActiveUser(principal);
        roomService.leaveRoom(roomID, user);
        mainController.showUpdate(model, principal);
        return "main";
    }
}
