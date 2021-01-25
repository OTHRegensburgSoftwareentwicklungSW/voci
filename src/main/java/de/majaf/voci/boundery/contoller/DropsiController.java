package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FileSystemObjectDTO;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.BinaryMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Scope(value = "singleton")
public class DropsiController {

    @Autowired
    private IDropsiService dropsiService;

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private RestTemplate restService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final String dropsiURL = "http://im-codd:8922/";

    @MessageMapping(value = "/download/{textChannelID}")
    @SendTo("/broker/{textChannelID}/receivedDropsiMessage")
    public Message sendDropsiFileDownloadRequest(@DestinationVariable("textChannelID") long textChannelID,
                                                 DropsiFileMessage msg,
                                                 Authentication auth) {
        try {
            User user = controllerUtils.getActiveUser(auth);
            if (user != null && channelService.userIsInChannel(textChannelID, user)) {
                try {
                    RegisteredUser sender = (RegisteredUser) userService.loadUserByID(msg.getSender().getId());
                    byte[] data = getFileFromID(msg.getDropsiFileId(), (sender.getDropsiToken()));
                    return dropsiService.updateBinary(msg, data);

                } catch (UserDoesNotExistException udnee) {
                    return new ErrorMessage("Sender of this file is not available anymore. SenderID: " + udnee.getUserID(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                } catch (DropsiException de) {
                    return new ErrorMessage("File was removed or problem connecting to Dropsi. Try again later.", HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                }
            } else throw new InvalidUserException(user, "User is not logged in or has no access to download.");
        } catch (UserDoesNotExistException udnee) {
            return new ErrorMessage("User with id: " + udnee.getUserID() + " could not be found", HttpServletResponse.SC_NOT_FOUND);
        } catch (InvalidUserException iue) {
            return new ErrorMessage("User " + iue.getUser().getUserName() + "is not allowed to download this file.", HttpServletResponse.SC_FORBIDDEN);
        }
    }

    public FolderDTO getRootFolder(String dropsiToken) throws DropsiException {
        FolderDTO rootFolder;
        try {
            rootFolder = restService.getForObject(dropsiURL + "api/rootfolder?secretKey=" + dropsiToken, FolderDTO.class);
        } catch (Exception e) {
            throw new DropsiException("No connection or no valid Dropsi-Token!", e);
        }
        return rootFolder;
    }

    public byte[] getFileFromID(long fileID, String dropsiToken) throws DropsiException {
        try {
            return restService.getForObject(dropsiURL + "api/files/" + fileID + "?secretKey=" + dropsiToken, byte[].class);
        } catch (Exception e) {
            throw new DropsiException("Problem connecting to Dropsi!", e);
        }
    }

    public void addDropsiFilesToModel(Model model, RegisteredUser user) {
        if (user.getDropsiToken() != null) {
            FolderDTO rootFolder;
            try {
                rootFolder = getRootFolder(user.getDropsiToken());
                model.addAttribute("fileList", dropsiService.getFilesFromRootFolder(rootFolder));
            } catch (DropsiException e) {
                model.addAttribute("dropsiErrorMsg", "Problems connecting to Dropsi. If this problem continues, please update your Dropsi-Token.");
            }
        }
    }
}
