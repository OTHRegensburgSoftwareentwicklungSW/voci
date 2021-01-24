package de.majaf.voci.boundery.contoller;

import de.majaf.voci.boundery.contoller.utils.ControllerUtils;
import de.majaf.voci.control.exceptions.call.DropsiException;
import de.majaf.voci.control.exceptions.user.InvalidUserException;
import de.majaf.voci.control.exceptions.user.UserDoesNotExistException;
import de.majaf.voci.control.service.IChannelService;
import de.majaf.voci.control.exceptions.channel.ChannelDoesNotExistException;
import de.majaf.voci.control.service.IUserService;
import de.majaf.voci.entity.DropsiFileMessage;
import de.majaf.voci.entity.Message;
import de.majaf.voci.entity.RegisteredUser;
import de.majaf.voci.entity.User;
import de.mschoettle.entity.dto.FileDTO;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller @Scope(value = "singleton")
public class TextChannelController {

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private IUserService userService;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private ControllerUtils controllerUtils;

    @MessageMapping("/{textChannelID}/{userID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID, @DestinationVariable long userID, String message) throws UserDoesNotExistException, ChannelDoesNotExistException {
        User user = userService.loadUserByID(userID);
        return channelService.createTextMessage(message, textChannelID, user);
    }

    @MessageMapping({"/{textChannelID}/sendDropsiFile"})
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendDropsiFile(@DestinationVariable long textChannelID, FileDTO file, Authentication auth) throws ChannelDoesNotExistException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) controllerUtils.getActiveUser(auth);
        return channelService.createDropsiFileMessage(file, textChannelID, user);
    }

    @RequestMapping(value = "/download/{textChannelID}/{senderID}/{dropsiFileId}/{dropsiFileName}")
    public void sendDropsiFileDownloadRequest(@PathVariable("textChannelID") long textChannelID,
                                              @PathVariable("senderID") long senderID,
                                              @PathVariable("dropsiFileId") long dropsiFileId,
                                              @PathVariable("dropsiFileName") String dropsiFileName,
                                              Authentication auth,
                                              HttpServletResponse response) throws UserDoesNotExistException, DropsiException, IOException, InvalidUserException {
        User user = controllerUtils.getActiveUser(auth);
        if(user != null && channelService.userIsInChannel(textChannelID, user)) {
            RegisteredUser sender = (RegisteredUser) userService.loadUserByID(senderID);
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + dropsiFileName);
            InputStream is = new ByteArrayInputStream(dropsiController.getFileFromID(dropsiFileId, sender.getDropsiToken()));
            IOUtils.copy(is, response.getOutputStream());
            response.getOutputStream().flush();
        } else throw new InvalidUserException(user, "User is not logged in or has no access to download.");
    }

}
