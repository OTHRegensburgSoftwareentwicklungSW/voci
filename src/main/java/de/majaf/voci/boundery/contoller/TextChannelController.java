package de.majaf.voci.boundery.contoller;

import de.majaf.voci.control.exceptions.call.DropsiException;
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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class TextChannelController {

    @Autowired
    @Qualifier("textChannelService")
    private IChannelService channelService;

    @Autowired
    private IUserService userService;

    @Autowired
    private DropsiController dropsiController;

    @Autowired
    private MainController mainController;

    @MessageMapping("/{textChannelID}/{userID}/sendMessage")
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendMessage(@DestinationVariable long textChannelID, @DestinationVariable long userID, String message) throws UserDoesNotExistException, ChannelDoesNotExistException {
        User user = userService.loadUserByID(userID);
        return channelService.createTextMessage(message, textChannelID, user);
    }

    @MessageMapping({"/{textChannelID}/sendDropsiFile"})
    @SendTo("/broker/{textChannelID}/receivedMessage")
    public Message sendDropsiFile(@DestinationVariable long textChannelID, FileDTO file, Authentication auth) throws DropsiException, ChannelDoesNotExistException, UserDoesNotExistException {
        RegisteredUser user = (RegisteredUser) mainController.getActiveUser(auth);
        return channelService.createDropsiFileMessage(file, textChannelID, user);
    }

    @RequestMapping("/call/download/")
    public byte[] sendDropsiFileDownloadRequest(@ModelAttribute("message") DropsiFileMessage message) throws DropsiException, UserDoesNotExistException {
        System.out.println(message.getDropsiFileId());
        RegisteredUser sender = (RegisteredUser) userService.loadUserByID(message.getSender().getId());
        return dropsiController.getFileFromID(message.getDropsiFileId(), sender.getDropsiToken());
    }

    @RequestMapping(value = "/call/download/{senderID}/{dropsiFileId}/{dropsiFileName}")
    public void sendDropsiFileDownloadRequest(@PathVariable("senderID") long senderID,
                                              @PathVariable("dropsiFileId") long dropsiFileId,
                                              @PathVariable("dropsiFileName") String dropsiFileName,
                                              HttpServletResponse response) throws UserDoesNotExistException, DropsiException, IOException {
        RegisteredUser sender = (RegisteredUser) userService.loadUserByID(senderID);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + dropsiFileName);
        InputStream is = new ByteArrayInputStream(dropsiController.getFileFromID(dropsiFileId, sender.getDropsiToken()));
        IOUtils.copy(is, response.getOutputStream());
        response.getOutputStream().flush();
    }

}
