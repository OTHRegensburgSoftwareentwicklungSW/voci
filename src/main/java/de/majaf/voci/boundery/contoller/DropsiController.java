package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.service.IDropsiService;
import de.majaf.voci.control.service.IMessageService;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.*;
import de.mschoettle.entity.dto.FolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Controller
@Scope(value = "singleton")
public class DropsiController {

    @Autowired
    private IMessageService messageService;

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
    private Logger logger;

    private final String dropsiURL = "http://im-codd:8922/";

    @MessageMapping(value = "/download/{textChannelID}")
    public void sendDropsiFileDownloadRequest(@DestinationVariable("textChannelID") long textChannelID,
                                              DropsiFileMessage msg,
                                              Authentication auth) {
        try {
            User user = controllerUtils.getActiveUser(auth);
            if (user != null)
                if (channelService.userIsInChannel(textChannelID, user)) {
                    try {
                        RegisteredUser sender = (RegisteredUser) userService.loadUserByID(msg.getSender().getId());
                        byte[] data = getFileFromID(msg.getDropsiFileId(), (sender.getDropsiToken()));
                        controllerUtils.sendSocketDownloadMessage(textChannelID, user.getId(), messageService.updateBinary(msg, data));
                    } catch (UserDoesNotExistException udnee) {
                        controllerUtils.sendSocketDownloadMessage(textChannelID, user.getId(),
                                new ErrorMessage("Sender of this file is not available anymore. SenderID: " + udnee.getUserID(), HttpServletResponse.SC_SERVICE_UNAVAILABLE));
                    } catch (DropsiException de) {
                        controllerUtils.sendSocketDownloadMessage(textChannelID, user.getId(),
                                new ErrorMessage("File was removed or problem connecting to Dropsi. Try again later.", HttpServletResponse.SC_SERVICE_UNAVAILABLE));
                    }
                } else controllerUtils.sendSocketDownloadMessage(textChannelID, user.getId(),
                        new ErrorMessage("User has no access to download.", HttpServletResponse.SC_FORBIDDEN));
            else throw new UserDoesNotExistException("User could not be found.");
        } catch (UserDoesNotExistException udnee) {
            // can not send error msg with sockets, because userID is unknown.
            // Can theoretically not occur
            logger.severe("User could not be found when downloading dropsi-file");
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
