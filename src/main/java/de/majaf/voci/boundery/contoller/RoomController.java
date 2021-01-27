package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.InvalidNameException;
import de.majaf.voci.control.exceptions.call.InvalidCallStateException;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.exceptions.channel.InvalidChannelException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IRoomService;
import de.majaf.voci.control.exceptions.room.RoomDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@Scope("session")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService textChannelService;

    @Autowired
    @Qualifier("voiceChannelService")
    private IChannelService voiceChannelService;

    @Autowired
    private IUserService userService;

    @Autowired
    ControllerUtils controllerUtils;

    @Autowired
    private MainController mainController;

    @Autowired
    private DropsiController dropsiController;

    private long activeTextChannelID = 0;

    @RequestMapping(value = "/room/{roomID}", method = RequestMethod.GET)
    public String prepareRoomPageAndTextChannel(@PathVariable("roomID") long roomID,
                                                @RequestParam("textChannelID") Optional<Long> textChannelID, Model model,
                                                Authentication auth) throws RoomDoesNotExistException, InvalidChannelException, InvalidUserException, ChannelDoesNotExistException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        if (roomService.roomHasAsMember(room, user)) {
            TextChannel textChannel = null;
            if (textChannelID.isPresent()) {
                textChannel = (TextChannel) textChannelService.loadChannelByIDInRoom(textChannelID.get(), room);
            } else {
                activeTextChannelID = 0;
            }
            dropsiController.addDropsiFilesToModel(model, user);
            addAttributesToModel(textChannel, user, room, model);
            return "room";
        } else throw new InvalidUserException(user, "User is no member");

    }

    @RequestMapping(value = "/room/{roomID}/{voiceChannelID}", method = RequestMethod.GET)
    public String joinVoiceChannel(@PathVariable("roomID") long roomID,
                                   @PathVariable("voiceChannelID") long voiceChannelID, Model model,
                                   Authentication auth) throws UserDoesNotExistException, RoomDoesNotExistException, InvalidChannelException, InvalidUserException, ChannelDoesNotExistException, InvalidCallStateException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);

        if (user.getActiveCall() != null) {
            // send Messages. Call is left in Function joinVoiceChannel.
            controllerUtils.sendSocketLeaveCallMessages(user, user.getActiveCall(), user.getActiveCall().getId());
        }

        if(user.getActiveVoiceChannel() != null) {
            // TODO: send Messages. VoiceChannel is left in Function joinVoiceChannel.
        }
        roomService.joinVoiceChannelInRoom(voiceChannelID, room, user);
        addAttributesToModel(null, user, room, model);
        return "room";
    }

    @RequestMapping(value = "/room/{roomID}/leaveVC", method = RequestMethod.GET)
    public String leaveVoiceChannel(@PathVariable("roomID") long roomID,
                                            Model model, Authentication auth) throws RoomDoesNotExistException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        userService.leaveVoiceChannel(user);
        addAttributesToModel(null, user, room, model);
        return "room";
    }

    @RequestMapping(value = "/room/{roomID}/invite", method = RequestMethod.GET)
    public String prepareRoomInvitationPage(@PathVariable("roomID") long roomID,
                                            Model model, Authentication auth) throws RoomDoesNotExistException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        model.addAttribute("user", user);
        model.addAttribute("room", room);
        return "roomInvite";
    }

    @RequestMapping(value = "/room/{roomID}/inviteContact", method = RequestMethod.POST)
    public String inviteContact(@PathVariable("roomID") long roomID, Model model, Authentication auth,
                                @RequestParam("contactID") long contactID) throws RoomDoesNotExistException, UserDoesNotExistException, InvalidUserException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        roomService.addMemberToRoom(room, contactID, user);
        dropsiController.addDropsiFilesToModel(model, user);
        addAttributesToModel(null, user, room, model);
        return "room";
    }

    @RequestMapping(value = "/room/{roomID}/removeMember", method = RequestMethod.DELETE)
    public String removeMember(@PathVariable("roomID") long roomID, Model model, Authentication auth, @RequestParam("memberID") long memberID) throws RoomDoesNotExistException, UserDoesNotExistException, InvalidUserException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        roomService.removeMemberFromRoom(room, memberID, user);
        dropsiController.addDropsiFilesToModel(model, user);
        model.addAttribute("textChannel", room.getTextChannels().get(0));
        model.addAttribute("user", user);
        model.addAttribute("room", room);
        return "room";
    }

    @RequestMapping(value = "/room/{roomID}/delete", method = RequestMethod.DELETE)
    public String deleteRoom(@PathVariable("roomID") long roomID, Model model, Authentication auth) throws RoomDoesNotExistException, InvalidUserException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        roomService.deleteRoom(roomID, user);
        mainController.showUpdate(model, user);
        return "main";
    }

    @RequestMapping(value = "/room/{roomID}/leave", method = RequestMethod.DELETE)
    public String leaveRoom(@PathVariable("roomID") long roomID, Model model, Authentication auth,
                            HttpServletResponse response) throws RoomDoesNotExistException, UserDoesNotExistException, IOException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        try {
            roomService.leaveRoom(roomID, user);
        } catch (InvalidUserException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User " + e.getUser().getUserName() + " can not leave. He is owner.");
        }
        mainController.showUpdate(model, user);
        return "main";
    }

    @RequestMapping(value = "/room/{roomID}/edit", method = RequestMethod.GET)
    public String prepareEditRoomPage(@PathVariable("roomID") long roomID, Model model, Authentication auth) throws RoomDoesNotExistException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        if (user.equals(room.getOwner())) {
            model.addAttribute("room", room);
            return "editRoom";
        } else throw new AccessDeniedException("User is not owner");
    }


    @RequestMapping(value = "/room/{roomID}/edit/addChannel", method = RequestMethod.POST)
    public String createChannel(@PathVariable("roomID") long roomID, @ModelAttribute("channelName") String channelName, @RequestParam("isTC") boolean isTC, Model model, Authentication auth) throws RoomDoesNotExistException, InvalidUserException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        try {
            if (isTC)
                textChannelService.addChannelToRoom(room, channelName, user);
            else
                voiceChannelService.addChannelToRoom(room, channelName, user);
        } catch (InvalidNameException e) {
            model.addAttribute("errorMsg", "Invalid channel name. " + e.getName());
        }
        model.addAttribute("room", room);
        return "editRoom";
    }

    @RequestMapping(value = "/room/{roomID}/edit/deleteChannel", method = RequestMethod.DELETE)
    public String deleteChannel(@PathVariable("roomID") long roomID,
                                @RequestParam("channelID") long channelID,
                                @RequestParam("isTC") boolean isTC, Model model,
                                Authentication auth) throws RoomDoesNotExistException, ChannelDoesNotExistException, InvalidUserException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        if (isTC)
            textChannelService.deleteChannelFromRoom(room, channelID, user);
        else
            voiceChannelService.deleteChannelFromRoom(room, channelID, user);
        model.addAttribute("room", room);
        return "editRoom";
    }

    @RequestMapping(value = "/room/{roomID}/edit/rename", method = RequestMethod.GET)
    public String prepareChannelRenamePage(@PathVariable("roomID") long roomID,
                                           @RequestParam("channelID") long channelID,
                                           @RequestParam("isTC") boolean isTC, Model model,
                                           Authentication auth) throws ChannelDoesNotExistException, RoomDoesNotExistException, InvalidChannelException, InvalidUserException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        if (user.equals(room.getOwner())) {
            Channel channel;
            if (isTC) {
                channel = textChannelService.loadChannelByIDInRoom(channelID, room);
            } else {
                channel = voiceChannelService.loadChannelByIDInRoom(channelID, room);
            }
            model.addAttribute("room", room);
            model.addAttribute("channel", channel);
        } else throw new InvalidUserException(user, "User is not Owner");
        return "renameChannel";
    }

    @RequestMapping(value = "/room/{roomID}/edit/rename", method = RequestMethod.POST)
    public String renameChannel(@PathVariable("roomID") long roomID, @RequestParam("channelID") long channelID,
                                @ModelAttribute("channelName") String channelName,
                                @RequestParam("isTC") boolean isTC,
                                Model model, Authentication auth) throws ChannelDoesNotExistException, RoomDoesNotExistException, InvalidUserException, UserDoesNotExistException, InvalidChannelException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        Room room = roomService.loadRoomByID(roomID);
        try {
            if (isTC) {
                textChannelService.renameChannel(channelID, room, channelName, user);
            } else {
                voiceChannelService.renameChannel(channelID, room, channelName, user);
            }
        } catch (InvalidNameException e) {
            model.addAttribute("errorMsg", "Invalid channel name. " + e.getName());
        }
        model.addAttribute("room", room);
        return "editRoom";
    }

    private void addAttributesToModel(TextChannel textChannel, RegisteredUser user, Room room, Model model) {
        if(textChannel != null) {
            activeTextChannelID = textChannel.getId();
        } else if(activeTextChannelID != 0) {
            try {
                textChannel = (TextChannel) textChannelService.loadChannelByID(activeTextChannelID);
            } catch (ChannelDoesNotExistException e) {
                // this Exception is possible, because Channels can be removed.
                textChannel = room.getTextChannels().get(0);
            }
        } else {
            textChannel = room.getTextChannels().get(0);
        }
        model.addAttribute("textChannel", textChannel);
        model.addAttribute("user", user);
        model.addAttribute("room", room);
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public void handleUserDoesNotExistException(HttpServletResponse response, UserDoesNotExistException e) throws IOException {
        controllerUtils.handleUserDoesNotExistException(response, e);
    }

    @ExceptionHandler(RoomDoesNotExistException.class)
    public void handleRoomDoesNotExistException(HttpServletResponse response, RoomDoesNotExistException e) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Room with id: " + e.getRoomID() + " could not be found");
    }

    @ExceptionHandler(ChannelDoesNotExistException.class)
    public void handleChannelDoesNotExistException(HttpServletResponse response, ChannelDoesNotExistException e) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Channel with id: " + e.getChannelID() + " could not be found");
    }

    @ExceptionHandler(InvalidChannelException.class)
    public void handleInvalidChannelException(HttpServletResponse response, InvalidChannelException e) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Channel with ID: " + e.getChannel().getChannelName() + " is not in room.");
    }

    @ExceptionHandler(InvalidUserException.class)
    public void handleInvalidUserException(HttpServletResponse response, InvalidUserException e) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "User with ID: " + e.getUser().getUserName() + " is not allowed to access this room.");
    }

    @ExceptionHandler(InvalidCallStateException.class)
    public void handleInvalidCallStateException(HttpServletResponse response, InvalidCallStateException e) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Call does not exist. Leaving failed"); // Can actually not happen.
    }
}
